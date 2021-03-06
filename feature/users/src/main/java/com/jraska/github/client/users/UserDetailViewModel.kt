package com.jraska.github.client.users

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.jraska.github.client.Config
import com.jraska.github.client.Owner
import com.jraska.github.client.analytics.AnalyticsEvent
import com.jraska.github.client.analytics.EventAnalytics
import com.jraska.github.client.common.lazyMap
import com.jraska.github.client.users.rx.toLiveData
import com.jraska.github.client.navigation.Navigator
import com.jraska.github.client.navigation.Urls
import com.jraska.github.client.rx.AppSchedulers
import com.jraska.github.client.users.model.RepoHeader
import com.jraska.github.client.users.model.UserDetail
import com.jraska.github.client.users.model.UsersRepository
import javax.inject.Inject

internal class UserDetailViewModel @Inject constructor(
  private val usersRepository: UsersRepository,
  private val schedulers: AppSchedulers,
  private val navigator: Navigator,
  private val eventAnalytics: EventAnalytics,
  private val config: Config
) : ViewModel() {

  private val liveData: Map<String, LiveData<ViewState>> = lazyMap(this::createUserLiveData)

  fun userDetail(login: String): LiveData<ViewState> {
    return liveData.getValue(login)
  }

  private fun createUserLiveData(login: String): LiveData<ViewState> {
    var reposInSection = config.getLong(USER_DETAIL_SECTION_SIZE_KEY).toInt()
    if (reposInSection <= 0) {
      reposInSection = 5
    }

    return usersRepository.getUserDetail(login, reposInSection)
      .subscribeOn(schedulers.io)
      .observeOn(schedulers.mainThread)
      .map<ViewState> { userDetail -> ViewState.DisplayUser(userDetail) }
      .onErrorReturn { ViewState.Error(it) }
      .startWith(ViewState.Loading)
      .toLiveData()
  }

  fun onUserGitHubIconClick(login: String) {
    val event = AnalyticsEvent.builder(ANALYTICS_OPEN_GITHUB)
      .addProperty("login", login)
      .build()

    eventAnalytics.report(event)

    navigator.launchOnWeb(Urls.user(login))
  }

  fun onRepoClicked(header: RepoHeader) {
    val event = AnalyticsEvent.builder(ANALYTICS_OPEN_REPO)
      .addProperty("owner", header.owner)
      .addProperty("name", header.name)
      .build()

    eventAnalytics.report(event)

    navigator.startRepoDetail(header.fullName())
  }

  sealed class ViewState {
    object Loading : ViewState()
    class Error(val error: Throwable) : ViewState()
    class DisplayUser(val user: UserDetail) : ViewState()
  }

  companion object {
    val USER_DETAIL_SECTION_SIZE_KEY = Config.Key("user_detail_section_size", Owner.USERS_TEAM)
    val ANALYTICS_OPEN_GITHUB = AnalyticsEvent.Key("open_github_from_detail", Owner.USERS_TEAM)
    val ANALYTICS_OPEN_REPO = AnalyticsEvent.Key("open_repo_from_detail", Owner.USERS_TEAM)
  }
}
