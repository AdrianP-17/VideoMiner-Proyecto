package aiss.dailymotionminer.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import aiss.dailymotionminer.dto.VMCaption;
import aiss.dailymotionminer.dto.VMChannel;
import aiss.dailymotionminer.dto.VMComment;
import aiss.dailymotionminer.dto.VMUser;
import aiss.dailymotionminer.dto.VMVideo;
import aiss.dailymotionminer.model.DailymotionVideo;
import aiss.dailymotionminer.model.DailymotionVideoResponse;

@Service
public class VideoMinerIntegrationService {

    @Autowired
    private DailymotionService dailymotionService;
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${videominer.base-url}")
    private String videoMinerApiBase;

    public Map<String, Object> fetchAndStoreChannelData(String userId, int maxVideos, int maxPages) {
        Map<String, Object> result = new HashMap<>();
        try {
            DailymotionVideoResponse videoResponse = dailymotionService.getVideosByUser(userId, maxVideos, maxPages);
            if (videoResponse == null || videoResponse.getVideos() == null) {
                result.put("status", "error"); result.put("message", "Failed to fetch videos from Dailymotion"); return result;
            }

            VMChannel channel = new VMChannel();
            channel.setId(userId);
            try {
                Map<String, Object> userDetails = restTemplate.getForObject("https://api.dailymotion.com/user/" + userId, Map.class);
                if (userDetails != null) {
                    channel.setName((String) userDetails.getOrDefault("screenname", userId));
                    channel.setDescription((String) userDetails.getOrDefault("description", "No description"));
                    Object createdTime = userDetails.get("created_time");
                    channel.setCreatedTime(createdTime != null ? new Date(((Number)createdTime).longValue() * 1000).toString() : new Date().toString());
                }
            } catch (Exception e) {
                channel.setName(userId); channel.setDescription("Channel from Dailymotion: " + userId); channel.setCreatedTime(new Date().toString());
            }

            List<VMVideo> videosList = new ArrayList<>();
            List<String> storedVideos = new ArrayList<>();
            
            for (DailymotionVideo dVideo : videoResponse.getVideos()) {
                VMVideo video = new VMVideo();
                video.setId(dVideo.getId()); video.setName(dVideo.getTitle());
                video.setDescription(dVideo.getDescription()); video.setReleaseTime(new Date(dVideo.getCreatedTime() * 1000).toString());

               if (dVideo.getOwnerId() != null) {
                    VMUser user = new VMUser();
                    user.setId(dVideo.getOwnerId() + "-" + dVideo.getId()); 
                    user.setName(dVideo.getOwnerUsername());
                    user.setUser_link(dVideo.getOwnerUrl()); 
                    user.setPicture_link(dVideo.getOwnerAvatarUrl());
                    video.setUser(user);
                }

                List<VMCaption> captionsList = new ArrayList<>();
                if (dVideo.getSubtitles() != null) {
                    for (DailymotionVideo.Subtitle subtitle : dVideo.getSubtitles()) {
                        VMCaption caption = new VMCaption();
                        caption.setId(UUID.randomUUID().toString()); caption.setLink(subtitle.getUrl());
                        caption.setLanguage(subtitle.getLanguage()); captionsList.add(caption);
                    }
                }
                video.setCaptions(captionsList);

                List<VMComment> commentsList = new ArrayList<>();
                if (dVideo.getTags() != null) {
                    for (String tag : dVideo.getTags()) {
                        VMComment comment = new VMComment();
                        comment.setId(UUID.randomUUID().toString()); comment.setText("Tag: " + tag);
                        comment.setCreatedOn(new Date().toString()); commentsList.add(comment);
                    }
                }
                video.setComments(commentsList);

                videosList.add(video); storedVideos.add(video.getId());
            }

            channel.setVideos(videosList);
            restTemplate.postForObject(videoMinerApiBase + "/channels", channel, VMChannel.class);

            result.put("status", "success"); result.put("message", "Data imported successfully from Dailymotion");
            result.put("videosImported", storedVideos.size()); result.put("channelId", userId);

        } catch (Exception e) {
            result.put("status", "error"); result.put("message", "Error: " + e.getMessage());
        }
        return result;
    }
}
