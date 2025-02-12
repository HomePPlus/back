package com.safehouse.common.service;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobClient;
import com.safehouse.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AzureBlobService {

    private final BlobContainerClient blobContainerClient;

    public String uploadImage(MultipartFile file) {
        try {
            // 고유한 파일명 생성
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            
            // Blob 클라이언트 생성
            BlobClient blobClient = blobContainerClient.getBlobClient(fileName);
            
            // 파일 업로드
            blobClient.upload(file.getInputStream(), file.getSize(), true);
            
            // 업로드된 파일의 URL 반환
            return blobClient.getBlobUrl();
            
        } catch (IOException e) {
            log.error("Failed to upload file to Azure Blob Storage", e);
            throw new CustomException.FileUploadException("파일 업로드에 실패했습니다.");
        }
    }

    public String uploadImageToTestFolder(MultipartFile file) {
        try {
            String fileName = "test_images/" + UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            BlobClient blobClient = blobContainerClient.getBlobClient(fileName);
            blobClient.upload(file.getInputStream(), file.getSize(), true);
            return blobClient.getBlobUrl();
        } catch (IOException e) {
            log.error("Failed to upload file to Azure Blob Storage test_images folder", e);
            throw new CustomException.FileUploadException("파일 업로드에 실패했습니다.");
        }
    }

    public void deleteImage(String fileName) {
        try {
            BlobClient blobClient = blobContainerClient.getBlobClient(fileName);
            if (blobClient.exists()) {
                blobClient.delete();
            }
        } catch (Exception e) {
            log.error("Failed to delete file from Azure Blob Storage", e);
            throw new CustomException.FileUploadException("파일 삭제에 실패했습니다.");
        }
    }
} 