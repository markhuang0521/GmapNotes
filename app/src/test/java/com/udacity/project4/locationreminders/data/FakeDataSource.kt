package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result


//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(private val reminders: MutableList<ReminderDTO>? = mutableListOf()) :
    ReminderDataSource {

    private var shouldReturnError = false


    fun setReturnError(boolean: Boolean) {
        shouldReturnError = boolean
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if (shouldReturnError) {
            return Result.Error("Test exception")
        }
        reminders?.let {
            return Result.Success(ArrayList(it))
        }
        return Result.Error("no reminders")

    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (shouldReturnError) {
            return Result.Error("Test exception")
        }
        reminders?.filter {
            it.id == id
        }?.let {
            return Result.Success(it[0])
        }
        return Result.Error("no reminders")
    }

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }

    override suspend fun updateReminder(reminder: ReminderDTO) {
        TODO("Not yet implemented")
    }


}