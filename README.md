# RedKarasMovie
An app to allow users to discover the most popular movies playing

A practise project for learning android development.

Use the API from themoviedb.org to fetch popular movies data.

For build the app you will need to create one account of "https://www.themoviedb.org" in order to request an API Key.

And then modify the file build.gradle in path app.

'''
buildTypes.each {
  it.buildConfigField 'String', 'THE_MOVIE_DB_API_KEY', theMoiveDBApiKey
}
'''

Replace 'theMoiveDBApiKey' to your API key.
