package com.udacity.project4.locationreminders.data

import androidx.lifecycle.MutableLiveData
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

class FakeDataRepository : ReminderDataSource {

    val reminderData = HashMap<String, ReminderDTO>()
    val reminderList = MutableLiveData<List<ReminderDTO>>()


    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return Result.Success(reminderData.values.toList())
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        TODO("Not yet implemented")
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllReminders() {
        TODO("Not yet implemented")
    }

    override suspend fun updateReminder(reminder: ReminderDTO) {
        TODO("Not yet implemented")
    }
}