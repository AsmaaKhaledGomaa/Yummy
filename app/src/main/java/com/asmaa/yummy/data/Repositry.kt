package com.asmaa.yummy.data

import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@ActivityRetainedScoped
class Repositry @Inject constructor(remoteDataSource: RemoteDataSource) {

    val remote = remoteDataSource
}