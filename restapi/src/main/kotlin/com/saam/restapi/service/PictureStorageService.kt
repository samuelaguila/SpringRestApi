package com.saam.restapi.service

import com.saam.restapi.exceptions.FileStorageException
import com.saam.restapi.exceptions.MyFileNotFoundException
import com.saam.restapi.model.Picture
import com.saam.restapi.dao.PictureDao
import com.saam.restapi.dao.PictureUriDao
import com.saam.restapi.model.PictureUri
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.IOException

@Service
class PictureStorageService {

    @Autowired
    private lateinit var pictureDao: PictureDao

    @Autowired
    private lateinit var pictureUriDao: PictureUriDao

    @Throws(FileStorageException::class)
    fun storePicture(picture: Picture): Picture {
        return try {
            pictureDao.save(picture)
        } catch (ex: IOException) {
            throw FileStorageException("Could not store file ${picture.pictureName} Please try again!", ex)
        }
    }

    @Throws(FileStorageException::class)
    fun storePictureUri(pictureUri: PictureUri) {
        try {
            pictureUriDao.save(pictureUri)
        } catch (ex: IOException) {
            throw FileStorageException("Could not store file ${pictureUri.pictureName} Please try again!", ex)
        }
    }

    @Throws(RuntimeException::class)
    fun getFile(fileId: String): Picture {
        return pictureDao.findById(fileId.toInt())
                .orElseThrow { MyFileNotFoundException("File not found with id $fileId") }
    }

    @Throws(RuntimeException::class)
    fun getAllFiles(): List<PictureUri> {
        return try {
            pictureUriDao.findAll()
        }catch (exception: MyFileNotFoundException){
            throw MyFileNotFoundException("Files not found")
        }
    }

//    @Throws(FileStorageException::class)
//    fun updateFile(picture: Picture) {
//        try {
//            pictureDao.save(picture)
//        }catch (exception: IOException){
//            throw FileStorageException("Files not found")
//        }
//    }

}