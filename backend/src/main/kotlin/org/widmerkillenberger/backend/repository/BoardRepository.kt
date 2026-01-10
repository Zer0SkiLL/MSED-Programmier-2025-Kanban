package org.widmerkillenberger.backend.repository

import org.widmerkillenberger.backend.model.entity.Board
import org.springframework.data.mongodb.repository.MongoRepository

interface BoardRepository : MongoRepository<Board, String>
