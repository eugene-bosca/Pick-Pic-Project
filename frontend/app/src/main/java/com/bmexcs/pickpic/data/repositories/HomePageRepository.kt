package com.bmexcs.pickpic.data.repositories

import com.bmexcs.pickpic.data.models.ListUserEventItem
import com.bmexcs.pickpic.data.sources.HomePageDataSource
import javax.inject.Inject

class HomePageRepository @Inject constructor(
    private val homePageDataSource: HomePageDataSource
) {
    suspend fun getEvents(userId: String): List<ListUserEventItem> {
        return homePageDataSource.getEvents(userId)
    }
}