package no.uio.ifi.in2000.team27.havapp.data.database

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import no.uio.ifi.in2000.team27.havapp.model.AppEntity
import no.uio.ifi.in2000.team27.havapp.model.cleaning.CleaningActivity
import no.uio.ifi.in2000.team27.havapp.model.user.User
import no.uio.ifi.in2000.team27.havapp.ui.impact.ImpactUiState
import no.uio.ifi.in2000.team27.havapp.ui.userProfile.UserProfileUiState

/**
 * DatabaseRepository er et repository for databasen.
 */

interface DatabaseRepository {
    // User functions
    suspend fun insertUser(username: String, avatarId: Int): Long
    suspend fun getUserById(userId: Long): User
    suspend fun updateUsername(userId: Long, username: String)
    suspend fun updateAvatarId(userId: Long, newAvatarId: Int)
    suspend fun logOut(): Int

    // Cleaning Activities functions
    suspend fun getCleaningActivitiesForUser(userId: Long): Flow<List<CleaningActivity>>
    suspend fun insertCleaningActivity(cleaningActivity: CleaningActivity): Long
    suspend fun getCleaningActivityById(id: Long): CleaningActivity
    suspend fun updateCleaningActivity(id: Long, cleaningActivity: CleaningActivity)
    suspend fun getTrashSummaryForUser(
        userId: Long,
        userProfileUiState: MutableStateFlow<UserProfileUiState>
    ): MutableMap<String, Int>

    suspend fun getActivitiesCountForUser(userId: Long, impactUiState: MutableStateFlow<ImpactUiState>): Int
}

class DatabaseRepositoryImpl(
    private val userDao: UserDao,
    private val cleaningActivityDao: CleaningActivityDao,
    private val appStateDao: AppStateDao
) : DatabaseRepository {

    /******************
     *  User Methods  *
     ******************/

    override suspend fun insertUser(username: String, avatarId: Int): Long {
        /** Insert a new user into the database based on the given username and avatarId.
         * The method also updates the AppState with the new userId.
         * @param username: String - the username of the new user
         * @param avatarId: Int - the avatarId of the new user
         * @return Long - the userId of the newly inserted user
         *
         * Used in:
         *  - OnboardingViewModel.generateUser(username: String, avatarId: Int)
         */

        // Create User object
        val user = User(username = username, avatarId = avatarId, lastLocation = "")

        // Insert user into database
        val userId = userDao.insert(user)

        // Update the AppState with the new onboarding status
        val appState = AppEntity(userId = userId, hasCompletedOnboarding = true)
        appStateDao.insert(appState)

        return userId
    }

    override suspend fun getUserById(userId: Long): User {
        /** Get a User object from the database based on the given userId.
         * @param userId: Long - the userId of the user to retrieve
         * @return User - the user with the given userId
         */

        // Get the user with the given userId from the database
        return userDao.getUserById(userId)
    }

    override suspend fun updateUsername(userId: Long, username: String) {
        /** Update the username of a user in the database.
         * @param userId: Long - the userId of the user to update
         * @param username: String - the new username of the user
         * @return Boolean - true if the update was successful, false otherwise
         *
         * Used in:
         *  - OnboardingViewModel.updateUsername(userId: Long, username: String)
         */

        // Update the username of the user with the given userId
        userDao.updateUsername(userId, username)
    }

    override suspend fun logOut(): Int {
        /** Log out the current user by deleting the user from the database.
         * Used in:
         *  - UserProfileViewModel.onClickLogOut()
         */

        userDao.deleteAll()
        appStateDao.deleteAll()
        return 1
    }

    override suspend fun updateAvatarId(userId: Long, newAvatarId: Int) {
        /** Update the avatarId of a user in the database.
         * @param userId: Long - the userId of the user to update
         * @param newAvatarId: Int - the new avatarId of the user
         * @return Boolean - true if the update was successful, false otherwise
         *
         * Used in:
         *  - OnboardingViewModel.updateAvatarId(userId: Long, avatarId: Int)
         */

        // Update the avatarId of the user with the given userId
        userDao.updateAvatarId(userId, newAvatarId)
    }

    /******************************
     *  CleaningActivity Methods  *
     ******************************/

    override suspend fun getCleaningActivitiesForUser(userId: Long): Flow<List<CleaningActivity>> {

        return cleaningActivityDao.getCleaningActivitiesForUser(userId)
    }

    override suspend fun insertCleaningActivity(cleaningActivity: CleaningActivity): Long {

        // Insert the CleaningActivity into the database
        return cleaningActivityDao.insert(cleaningActivity)
    }

    override suspend fun getCleaningActivityById(id: Long): CleaningActivity {
        /** Get a CleaningActivity object from the database based on the given id.
         * @param id: Int - the id of the CleaningActivity to retrieve
         * @return CleaningActivity - the CleaningActivity with the given id
         *
         * Used in:
         *  - CleaningActivityViewModel.loadCleaningActivityIntoUiState(id: Int)
         */

        // Get the CleaningActivity with the given id from the database
        return cleaningActivityDao.getCleaningActivityById(id)
    }

    override suspend fun updateCleaningActivity(id: Long, cleaningActivity: CleaningActivity) {
        /** Update a CleaningActivity in the database.
         * @param id: Int - the id of the CleaningActivity to update
         * @param cleaningActivity: CleaningActivity - the new CleaningActivity object
         *
         * Used in:
         *  - CleaningActivityViewModel.updateCleaningActivity(id: Int, cleaningActivity: CleaningActivity)
         */

        // Update the CleaningActivity with the given id

        cleaningActivityDao.updateCleaningActivityById(id, cleaningActivity)
    }

    override suspend fun getTrashSummaryForUser(
        userId: Long,
        userProfileUiState: MutableStateFlow<UserProfileUiState>
    ): MutableMap<String, Int> {
        /** Get a map that contains the total amount of trash cleaned for each trash type
         * @param userId: Long - User ID
         *
         * @return trashSummary: Map<String, Int> - a map of the summarised trash counts
         */

        val summary: MutableMap<String, Int> = mutableMapOf(
            "Plast" to 0,
            "Fiskeutstyr" to 0,
            "Sigaretter" to 0,
            "Annet" to 0
        )

        // Get all activities
        getCleaningActivitiesForUser(userId)
            .catch { exception ->
                Log.e(
                    "Error",
                    exception.toString()
                )
            }.collect { cleaningActivities ->
                // Sort out empty activities (where no trash is counted)
                val filteredActivities = cleaningActivities.filter { cleaningActivity ->
                    cleaningActivity.trash.values.any { amount -> amount > 0 }
                }

                // For all activities...
                filteredActivities.forEach { cleaningActivity ->
                    // For all trash types...
                    cleaningActivity.trash.forEach { (trash, count) ->
                        // Add the count to the summary
                        summary[trash] = summary[trash]!! + count
                    }
                }
                userProfileUiState.update { currentState ->
                    currentState.copy(
                        cleaningSummary = summary
                    )
                }

            }
        return summary
    }

    override suspend fun getActivitiesCountForUser(
        userId: Long,
        impactUiState: MutableStateFlow<ImpactUiState>
    ): Int {
        /**
         * Get the amount of non-empty cleaning activities for the user.
         * @param userId: Long - user to fetch for
         *
         * @return Int - amount of non-empty cleaning activities. Returns 0 if none is found.
         */
        var count = 0
        getCleaningActivitiesForUser(userId)
            .catch { exception ->
                Log.e(
                    "Error",
                    exception.toString()
                )
            }.collect { cleaningActivities ->
                // Sort out empty activities (where no trash is counted)
                val filteredActivities = cleaningActivities.filter { cleaningActivity ->
                    cleaningActivity.trash.values.any { amount -> amount > 0 }
                }

                // For all activities...
                filteredActivities.forEach { _ ->
                    count++
                }

                impactUiState.update { currentState ->
                    currentState.copy(
                        impactCounter = count
                    )
                }

            }
        return count
    }
}
