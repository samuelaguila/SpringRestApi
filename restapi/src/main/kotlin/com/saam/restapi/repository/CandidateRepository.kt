package com.saam.restapi.repository

import com.saam.restapi.model.Candidate
import org.springframework.data.jpa.repository.JpaRepository

interface CandidateRepository: JpaRepository<Candidate, Int>

