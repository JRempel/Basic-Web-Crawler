# Basic Web Crawler

### Building
- *(Optional)* update config.properties in web-crawler/src/main/resources.
- Run "mvn clean install" from the root.

### Running
- Execute start-server.cmd in /htdocs

### Implemented Features
- Reads settings from .properties file
- Retrieves links & important words from URLS in the pool
- Stores results in an H2 embedded DB
- Returns results as an intersect of all search-terms results.

### Planned Features / Items
- Return partial results when no matches for all search-terms
- Obey robots.txt
- Ranking of results
- Store timestamps & recrawl periodically
- Multithreaded crawling & storage?
