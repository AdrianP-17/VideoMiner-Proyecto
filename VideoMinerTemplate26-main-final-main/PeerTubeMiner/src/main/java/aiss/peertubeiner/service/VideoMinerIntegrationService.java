package aiss.peertubeiner.service;

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

import aiss.peertubeiner.dto.VMCaption;
import aiss.peertubeiner.dto.VMChannel;
import aiss.peertubeiner.dto.VMComment;
import aiss.peertubeiner.dto.VMUser;
import aiss.peertubeiner.dto.VMVideo;
import aiss.peertubeiner.model.PeerTubeCommentThreadResponse;
import aiss.peertubeiner.model.PeerTubeVideo;
import aiss.peertubeiner.model.PeerTubeVideoResponse;

@Service
public class VideoMinerIntegrationService {
    @Autowired
    private PeerTubeService peerTubeService;
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Value("${videominer.base-url}")
    private String videoMinerApiBase;

    @Value("${peertube.base-url}")
    private String peertubeApiBase;

    public Map<String, Object> fetchAndStoreChannelData(String channelHandle, int maxVideos, int maxComments) {
        Map<String, Object> result = new HashMap<>();
        try {
            // Ya le pasamos maxComments para que nos lo traiga todo hecho
            PeerTubeVideoResponse videoResponse = peerTubeService.getVideosByChannel(channelHandle, maxVideos, maxComments);
            if (videoResponse == null || videoResponse.getVideos() == null) {
                result.put("status", "error"); result.put("message", "Failed to fetch videos from PeerTube"); return result;
            }

            VMChannel channel = new VMChannel();
            channel.setId(channelHandle); channel.setName(channelHandle);
            channel.setDescription("Channel from PeerTube: " + channelHandle);
            channel.setCreatedTime(new Date().toString());
            
            List<VMVideo> videosList = new ArrayList<>();
            List<String> storedVideosIds = new ArrayList<>();
            
            for (PeerTubeVideo pVideo : videoResponse.getVideos()) {
                VMVideo video = new VMVideo();
                video.setId(pVideo.getUuid()); video.setName(pVideo.getName());
                video.setDescription(pVideo.getDescription()); video.setReleaseTime(pVideo.getPublishedAt());

                // Mapeo Usuario y Avatar reconstruido
                if (pVideo.getAccount() != null) {
                    VMUser user = new VMUser();
                    user.setId(pVideo.getAccount().getId().toString() + "-" + pVideo.getUuid()); user.setName(pVideo.getAccount().getName());
                    user.setUser_link(pVideo.getAccount().getUrl());
                    
                    if (pVideo.getAccount().getAvatars() != null && !pVideo.getAccount().getAvatars().isEmpty()) {
                        String path = pVideo.getAccount().getAvatars().get(0).getPath();
                        // Transformamos "/api/v1" a la URL real (ej. "https://peertube.tv" + "/lazy-static/...")
                        String baseUrl = peertubeApiBase.replace("/api/v1", "");
                        user.setPicture_link(baseUrl + path);
                    }
                    video.setUser(user);
                }

                // Mapeo Comentarios (Ya pre-cargados)
                List<VMComment> commentsList = new ArrayList<>();
                if (pVideo.getComments() != null) {
                    for (PeerTubeCommentThreadResponse.CommentThread thread : pVideo.getComments()) {
                        VMComment comment = new VMComment();
                        comment.setId(thread.getId().toString()); comment.setText(thread.getText());
                        comment.setCreatedOn(thread.getCreatedAt()); commentsList.add(comment);
                    }
                }
                video.setComments(commentsList);

                // Mapeo Captions (Ya pre-cargados)
                List<VMCaption> captionsList = new ArrayList<>();
                if (pVideo.getCaptions() != null && pVideo.getCaptions().getData() != null) {
                    for (PeerTubeVideo.Caption pCaption : pVideo.getCaptions().getData()) {
                        VMCaption caption = new VMCaption();
                        caption.setId(UUID.randomUUID().toString()); caption.setLink(pCaption.getFileUrl());
                        if (pCaption.getLanguage() != null) { caption.setLanguage(pCaption.getLanguage().getId()); }
                        captionsList.add(caption);
                    }
                }
                video.setCaptions(captionsList);

                videosList.add(video); storedVideosIds.add(video.getId());
            }

            channel.setVideos(videosList);
            restTemplate.postForObject(videoMinerApiBase + "/channels", channel, VMChannel.class);

            result.put("status", "success"); result.put("message", "Data imported successfully from PeerTube");
            result.put("videosImported", storedVideosIds.size()); result.put("channelId", channelHandle);
        } catch (Exception e) {
            result.put("status", "error"); result.put("message", "Error: " + e.getMessage());
        }
        return result;
    }
}