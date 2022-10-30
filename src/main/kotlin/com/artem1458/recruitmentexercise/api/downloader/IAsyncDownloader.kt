package com.artem1458.recruitmentexercise.api.downloader

import java.net.URL
import java.util.concurrent.Future

interface IAsyncDownloader<T> {
  fun download(url: URL): Future<T>
}
