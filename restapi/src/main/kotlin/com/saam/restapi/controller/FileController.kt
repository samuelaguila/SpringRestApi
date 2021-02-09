package com.saam.restapi.controller

import com.saam.restapi.exceptions.FileStorageException
import com.saam.restapi.model.Picture
import com.saam.restapi.model.PictureUri
import com.saam.restapi.payload.UploadFileResponse
import com.saam.restapi.service.PictureStorageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.io.IOException
import java.util.HashMap

@RestController
class FileController {

    @Autowired
    private lateinit var storageService: PictureStorageService

    @GetMapping("/list")
    @Throws(IOException::class)
    fun listAll(): Map<String, List<PictureUri>> {
        val response: MutableMap<String, List<PictureUri>> = HashMap<String, List<PictureUri>>()
        response["casas"] = storageService.getAllFiles()
        return response
    }

    @GetMapping("/downloadFile/{fileId}")
    fun downloadFile (@PathVariable fileId: String): ResponseEntity<Resource> {

    // Load file from database
    val candidate = storageService.getFile(fileId)

    return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(candidate.pictureType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + candidate.pictureName + "\""
            )
            .body(ByteArrayResource(candidate.data))
    }

    @PostMapping("/uploadFile")
    fun uploadFile(
            @RequestParam("file") file: MultipartFile
    ): ResponseEntity<UploadFileResponse> {

        // Normalize file name
        val fileName = StringUtils.cleanPath(file.originalFilename!!)

        // Check if the file's name contains invalid characters
        if (fileName.contains("..")) {
            throw FileStorageException("Sorry! Filename contains invalid path sequence $fileName")
        }

        val picture = Picture(
                pictureName = fileName,
                pictureType = file.contentType!!,
                data = file.bytes
        )

        val savedPicture= storageService.storePicture(picture)

        val fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloadFile/")
                .path(savedPicture.id.toString())
                .toUriString()

        val pictureUri= PictureUri(
                pictureName = fileName,
                pictureType = file.contentType!!,
                pictureUri = fileDownloadUri
        )

        storageService.storePictureUri(pictureUri)

        return ResponseEntity(UploadFileResponse(
                file.name,
                fileDownloadUri,
                file.contentType),
                HttpStatus.OK)
    }

//    @Throws(FileStorageException::class)
//    @PutMapping("/update")
//    fun updateFile(picture: Picture) {
//
//        try {
//            storageService.updateFile(picture)
//
//        } catch (ex: IOException) {
//            throw FileStorageException("Could not store file ${picture.pictureName} Please try again!", ex)
//        }
//    }
}