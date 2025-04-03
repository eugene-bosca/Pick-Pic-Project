package com.bmexcs.pickpic.data.sources

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RealDataSource

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CacheImageDataSource