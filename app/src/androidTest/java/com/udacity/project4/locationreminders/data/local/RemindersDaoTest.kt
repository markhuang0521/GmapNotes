package com.udacity.project4.locationreminders.data.local

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {
    private lateinit var remindersDao: RemindersDao
    private lateinit var db: RemindersDatabase

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, RemindersDatabase::class.java
        ).build()
        remindersDao = db.reminderDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun saveReminder_returnId() = runBlockingTest {

        val reminder = ReminderDTO("title", "desc", "LA", 21.1, 21.1)
        remindersDao.saveReminder(reminder)
        val byId = remindersDao.getReminderById(reminder.id)!!
        assertThat(byId.title, equalTo(reminder.title))
        assertThat(byId, notNullValue())
        assertThat(byId.description, equalTo(reminder.description))
        assertThat(byId.location, equalTo(reminder.location))
        assertThat(byId.longitude, equalTo(reminder.longitude))
        assertThat(byId.latitude, equalTo(reminder.latitude))
        assertThat(byId.id, equalTo(reminder.id))
    }

    @Test
    fun getReminders_returnList() = runBlockingTest {
        val reminder = ReminderDTO("title", "desc", "LA", 21.1, 21.1)
        val reminder2 = ReminderDTO("title2", "desc2", "NY", 21.1, 21.1)
        val reminder3 = ReminderDTO("title3", "desc3", "TX", 21.1, 21.1)

        remindersDao.saveReminder(reminder)
        remindersDao.saveReminder(reminder2)
        remindersDao.saveReminder(reminder3)

        val list = remindersDao.getReminders()

        assertThat(list, notNullValue())
        assertThat(list, hasItem(reminder))
        assertThat(list, hasItem(reminder2))
        assertThat(list, hasItem(reminder3))

    }

    @Test
    fun deleteReminders_returnNull() = runBlockingTest {
        val reminder = ReminderDTO("title", "desc", "LA", 21.1, 21.1)
        val reminder2 = ReminderDTO("title2", "desc2", "NY", 21.1, 21.1)
        val reminder3 = ReminderDTO("title3", "desc3", "TX", 21.1, 21.1)

        remindersDao.saveReminder(reminder)
        remindersDao.saveReminder(reminder2)
        remindersDao.saveReminder(reminder3)
        remindersDao.deleteAllReminders()

        val list = remindersDao.getReminders()
        assertThat(list.size, equalTo(0))


    }


}