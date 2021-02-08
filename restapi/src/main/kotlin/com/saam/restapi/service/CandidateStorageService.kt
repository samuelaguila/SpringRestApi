package com.saam.restapi.service

import com.saam.restapi.exceptions.FileStorageException
import com.saam.restapi.exceptions.MyFileNotFoundException
import com.saam.restapi.model.Candidate
import com.saam.restapi.payload.UploadFileResponse
import com.saam.restapi.repository.CandidateRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.io.IOException

@Service
class CandidateStorageService {

    @Autowired
    private lateinit var candidateRepository: CandidateRepository

    @Throws(FileStorageException::class)
    fun storeFile(candidate: Candidate): Candidate {
        return try {
            candidateRepository.save(candidate)
        } catch (ex: IOException) {
            throw FileStorageException("Could not store file ${candidate.pictureName} Please try again!", ex)
        }
    }


    @Throws(RuntimeException::class)
    fun getFile(fileId: String): Candidate {
        return candidateRepository.findById(fileId.toInt())
                .orElseThrow { MyFileNotFoundException("File not found with id $fileId") }
    }

    @Throws(RuntimeException::class)
    fun getAllFiles(): MutableList<Candidate> {
        return try {
            val result: MutableList<Candidate> = candidateRepository.findAll()
            result
        }catch (exception: MyFileNotFoundException){
            throw MyFileNotFoundException("Files not found")
        }
    }

    @Throws(FileStorageException::class)
    fun updateFile(candidate: Candidate) {
        try {
            candidateRepository.save(candidate)
        }catch (exception: IOException){
            throw FileStorageException("Files not found")
        }
    }
}