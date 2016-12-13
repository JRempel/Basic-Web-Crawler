# Web Crawler & Basic Search Engine

### Building
- *(Optional)* update config.properties in web-crawler/src/main/resources.
- Run "mvn clean install" from the root.

### Running
- Execute start-server.cmd in /htdocs

### Implemented Features
- Reads settings from .properties file
- Retrieves links & important words from URLS in the pool
- Stores results in an H2 embedded DB
- Returns results as an intersect of all search-terms results
- Stores timestamps for each URL, has 'daysBeforeRecrawl' parameter
- Basic ranking of results; for each word: number of occurrences > first occurrence > alphabetical sort on URL

### Planned Features / Items
- Return partial results when no matches for all search-terms
- Obey robots.txt
- Recrawl periodically
- Multithreaded crawling & storage?
