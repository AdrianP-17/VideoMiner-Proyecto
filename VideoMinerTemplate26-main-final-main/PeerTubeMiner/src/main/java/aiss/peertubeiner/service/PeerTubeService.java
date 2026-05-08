package aiss.peertubeiner.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import aiss.peertubeiner.model.PeerTubeCommentThreadResponse;
import aiss.peertubeiner.model.PeerTubeVideo;
import aiss.peertubeiner.model.PeerTubeVideoResponse;

@Service
public class PeerTubeService {

    private final RestTemplate restTemplate;
    
    @Value("${peertube.base-url}")
    private String peertubeApiBase;

    // CONSTRUCTOR: Disfrazamos a nuestro minero de Java como si fuera Google Chrome
    public PeerTubeService() {
        this.restTemplate = new RestTemplate();
        this.restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            request.getHeaders().set("Accept", "application/json");
            return execution.execute(request, body);
        });
    }

    // MÉTODO PRINCIPAL
    public PeerTubeVideoResponse getVideosByChannel(String id, int maxVideos, int maxComments) {
        PeerTubeVideoResponse response = null;

        System.out.println("🔍 Iniciando búsqueda en PeerTube para el ID: [" + id + "]");

        try {
            // INTENTO 1: Buscar en el endpoint de "Canales"
            String urlChannel = peertubeApiBase + "/video-channels/" + id + "/videos?count=" + maxVideos;
            System.out.println("🌐 Probando URL Canales: " + urlChannel);
            response = restTemplate.getForObject(urlChannel, PeerTubeVideoResponse.class);
        } catch (Exception e) {
            System.out.println("⚠️ Fallo en canales (" + e.getMessage() + "). Probando como cuenta...");
        }

        // Si el intento 1 falló (dio 404) o devolvió una lista vacía, hacemos el INTENTO 2
        if (response == null || response.getVideos() == null || response.getVideos().isEmpty()) {
            try {
                // INTENTO 2: Buscar en el endpoint de "Cuentas"
                String urlAccount = peertubeApiBase + "/accounts/" + id + "/videos?count=" + maxVideos;
                System.out.println("🌐 Probando URL Cuentas: " + urlAccount);
                response = restTemplate.getForObject(urlAccount, PeerTubeVideoResponse.class);
            } catch (Exception e) {
                // ESTE ES EL LOG QUE NECESITAMOS VER
                System.err.println("❌ ERROR FATAL DE PEERTUBE: " + e.getMessage());
                return null;
            }
        }

        // LA MAGIA: Pedir extras por cada vídeo encontrado
        try {
            if (response != null && response.getVideos() != null) {
                System.out.println("✅ Se encontraron " + response.getVideos().size() + " vídeos. Extrayendo extras...");
                for (PeerTubeVideo video : response.getVideos()) {
                    
                    // 1. Extraer Subtítulos
                    PeerTubeVideo.CaptionResponse capResponse = getCaptionsByVideo(video.getUuid());
                    if (capResponse != null) {
                        video.setCaptions(capResponse);
                    }
                    
                    // 2. Extraer Comentarios
                    PeerTubeCommentThreadResponse comResponse = getCommentsByVideo(video.getUuid(), maxComments);
                    if (comResponse != null && comResponse.getThreads() != null) {
                        video.setComments(comResponse.getThreads());
                    }
                }
            }
            return response;
        } catch (Exception e) {
            System.err.println("❌ Error procesando los extras del vídeo: " + e.getMessage());
            return null;
        }
    }

    // MÉTODO EXTRA 1: Comentarios
    public PeerTubeCommentThreadResponse getCommentsByVideo(String videoUuid, int maxComments) {
        String url = peertubeApiBase + "/videos/" + videoUuid + "/comment-threads?count=" + maxComments;
        try {
            return restTemplate.getForObject(url, PeerTubeCommentThreadResponse.class);
        } catch (Exception e) {
            return null;
        }
    }

    // MÉTODO EXTRA 2: Subtítulos
    public PeerTubeVideo.CaptionResponse getCaptionsByVideo(String videoUuid) {
        String url = peertubeApiBase + "/videos/" + videoUuid + "/captions";
        try {
            return restTemplate.getForObject(url, PeerTubeVideo.CaptionResponse.class);
        } catch (Exception e) {
            return null;
        }
    }

    // MÉTODO EXTRA 3: Vídeo individual
    public PeerTubeVideo getSingleVideo(String videoId) {
        String url = peertubeApiBase + "/videos/" + videoId;
        try {
            return restTemplate.getForObject(url, PeerTubeVideo.class);
        } catch (Exception e) {
            return null;
        }
    }
}
