package com.buy.together.data.dao

import androidx.room.*

@Dao
interface UserDao {

//    @Query("SELECT * FROM note")
//    suspend fun getNotes() : MutableList<NotesDto>
//
//    @Query("SELECT * FROM note WHERE ID=(:id)")
//    suspend fun getNote(id: Long) : NotesDto
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertNote(dto : NotesDto)
//
//    @Insert
//    fun insertNotes(vararg notes:NotesDto)
//
//    @Insert
//    fun insertBothNotes(note1:NotesDto, note2:NotesDto)
//
//    @Insert
//    fun insertNotesAndBooks(note:NotesDto, book: List<NotesDto>)
//
//    @Update
//    suspend fun updateNote(dto : NotesDto)
//
//    @Update
//    fun updateNotes(vararg notes:NotesDto)
//
//    @Delete
//    suspend fun deleteNote(dto : NotesDto)
//
//    @Delete
//    fun deleteNotes(vararg notes:NotesDto)
//
//    @Query("DELETE FROM note WHERE ID=(:id)")
//    fun deleteNoteById(id : Long)
}