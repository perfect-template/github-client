package com.jraska.github.client.users.ui

import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.EpoxyModel
import com.jraska.github.client.users.R
import com.jraska.github.client.users.model.RepoDetail
import org.threeten.bp.format.DateTimeFormatter

internal class RepoDetailHeaderModel(private val repoDetail: RepoDetail) : EpoxyModel<View>() {

  override fun getDefaultLayout(): Int {
    return R.layout.item_repo_detail_stats
  }

  override fun bind(itemView: View) {
    val createdText = CREATED_DATE_FORMAT.format(repoDetail.data.created)
    itemView.findViewById<TextView>(R.id.repo_detail_created).text = createdText

    itemView.findViewById<TextView>(R.id.repo_detail_subscribers_count).text = repoDetail.data.subscribersCount.toString()
    itemView.findViewById<TextView>(R.id.repo_detail_forks_count).text = repoDetail.header.forks.toString()
    itemView.findViewById<TextView>(R.id.repo_detail_stars_count).text = repoDetail.header.stars.toString()
  }

  companion object {
    val CREATED_DATE_FORMAT = DateTimeFormatter.ISO_INSTANT!!
  }
}
