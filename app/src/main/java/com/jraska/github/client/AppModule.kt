package com.jraska.github.client

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco
import com.jakewharton.threetenabp.AndroidThreeTen
import com.jraska.github.client.core.android.OnAppCreate
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet

@Module
object AppModule {

  @Provides
  @IntoSet
  fun setupFresco(): OnAppCreate {
    return object : OnAppCreate {
      override fun onCreate(app: Application) = Fresco.initialize(app)
    }
  }

  @Provides
  @IntoSet
  fun setupThreeTen(): OnAppCreate {
    return object : OnAppCreate {
      override fun onCreate(app: Application) = AndroidThreeTen.init(app)
    }
  }
}
