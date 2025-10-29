package com.seatify.util;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class FileUploadUtil {

    @Autowired
    private Cloudinary cloudinary;

    /**
     * Upload a file to Cloudinary
     * @param file The multipart file to upload
     * @param folder The folder to upload to (optional)
     * @return The public URL of the uploaded file
     * @throws IOException if upload fails
     */
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        try {
            // Log Cloudinary configuration for debugging
            log.info("Uploading file to Cloudinary with folder: {}", folder);
            log.info("File size: {} bytes", file.getSize());
            log.info("File content type: {}", file.getContentType());
            
            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "folder", folder != null ? folder : "seatify",
                    "resource_type", "auto"
            );
            
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
            String publicUrl = (String) uploadResult.get("secure_url");
            
            log.info("File uploaded successfully to Cloudinary: {}", publicUrl);
            return publicUrl;
            
        } catch (Exception e) {
            log.error("Error uploading file to Cloudinary: {}", e.getMessage());
            log.error("Full error details: ", e);
            throw new IOException("Failed to upload file to Cloudinary: " + e.getMessage(), e);
        }
    }

    /**
     * Upload a file to Cloudinary with default folder
     * @param file The multipart file to upload
     * @return The public URL of the uploaded file
     * @throws IOException if upload fails
     */
    public String uploadFile(MultipartFile file) throws IOException {
        return uploadFile(file, "seatify");
    }

    /**
     * Delete a file from Cloudinary using its public URL
     * @param publicUrl The public URL of the file to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteFile(String publicUrl) {
        try {
            // Extract public ID from URL
            String publicId = extractPublicIdFromUrl(publicUrl);
            if (publicId == null) {
                log.warn("Could not extract public ID from URL: {}", publicUrl);
                return false;
            }
            
            Map<String, Object> deleteResult = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            String result = (String) deleteResult.get("result");
            
            boolean success = "ok".equals(result);
            if (success) {
                log.info("File deleted successfully from Cloudinary: {}", publicId);
            } else {
                log.warn("Failed to delete file from Cloudinary: {}", publicId);
            }
            
            return success;
            
        } catch (Exception e) {
            log.error("Error deleting file from Cloudinary: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extract public ID from Cloudinary URL
     * @param url The Cloudinary URL
     * @return The public ID or null if extraction fails
     */
    private String extractPublicIdFromUrl(String url) {
        try {
            // Cloudinary URL format: https://res.cloudinary.com/{cloud_name}/image/upload/v{version}/{public_id}.{format}
            String[] parts = url.split("/");
            if (parts.length >= 2) {
                String lastPart = parts[parts.length - 1];
                // Remove file extension
                int dotIndex = lastPart.lastIndexOf('.');
                if (dotIndex > 0) {
                    return lastPart.substring(0, dotIndex);
                }
                return lastPart;
            }
        } catch (Exception e) {
            log.error("Error extracting public ID from URL: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Upload file with specific transformation parameters
     * @param file The multipart file to upload
     * @param folder The folder to upload to
     * @param transformations Transformation parameters (e.g., width, height, crop)
     * @return The public URL of the uploaded file
     * @throws IOException if upload fails
     */
    public String uploadFileWithTransformations(MultipartFile file, String folder, Map<String, Object> transformations) throws IOException {
        try {
            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "folder", folder != null ? folder : "seatify",
                    "resource_type", "auto"
            );
            
            // Add transformations if provided
            if (transformations != null && !transformations.isEmpty()) {
                uploadParams.putAll(transformations);
            }
            
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
            String publicUrl = (String) uploadResult.get("secure_url");
            
            log.info("File uploaded successfully to Cloudinary with transformations: {}", publicUrl);
            return publicUrl;
            
        } catch (IOException e) {
            log.error("Error uploading file to Cloudinary with transformations: {}", e.getMessage());
            throw new IOException("Failed to upload file to Cloudinary", e);
        }
    }
}
