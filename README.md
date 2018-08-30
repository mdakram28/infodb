# InfoDB

InfoDB is a Information crawler and extraction tool for retreiving and storing information about people from the internet.

### Installation

InfoDB requires [java 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) to run.
Step 1. Install Java 8
Step 2. Install Maven 3+
Step 2. Install MongoDB
Step 3. Install [chromedriver and chrome](https://tecadmin.net/setup-selenium-chromedriver-on-ubuntu/)
Step 4. Clone InfoDB repository
Step 5. Install Maven depdendencies and build
```sh
$ cd infodb                 # Go into project directory
$ mvn install               # Install maven depdency, build jar, test
```
Step 6. Setup MongoDB Database
```sh
$ cd infodb                 # Go into project directory
$ mongod                    # start mongodb server
$ mongo < mongodb.txt       # configure mongodb database
```
Edit `src/main/resources/application.properties`  for changing application configuration

### Configuration

The application can be configured by supplying the following variables in properties file.

| Name | Description | Values |
| ------ | ------ | ------ |
| server.port | Port to run the REST Service | 8080 |
| spring.profiles.active | Active environment  | dev/prod | 
| spring.data.mongodb.host | MongoDB Host | host address |
| spring.data.mongodb.port | MonogDB Port | 27017 |
| spring.data.mongodb.database | MongoDB Database name | infodb |
| NosqlDbStore.enabled | Enable or Disable MongoDB | true/false |
| GoogleScraper.enabled | Enable or Disable Google Scraper | true/false |
| GithubAPI.enabled | Enable or Disable Github API client | true/false |
| PersonExtractor.enabled | Enable or Disable Person Knowledge Engine | true/false |
| <EndpointClassName>.enabled | Enable or Disable DataEndpoint | true/false |
| titlediff_threshold | Threshold of difference in found title and queried title | 0 - MAX_INT |
| infofetch_batchsize | Batch size to process the names | 1 - MAX_INT |

<EndpointClassName>.enabled default value is true

### Running

```sh
$ cd infodb                 # Go into project directory
$ # build by running 'mvn install' if required
$ java -jar target/infodb-0.0.1-SNAPSHOT.jar
```

In order to override these properties during runtime write the properties in a file and provide the relative path of the file as below

```sh
$ java -Dspring.config.location=<relative/path/to/application.properties> -jar target/infodb-0.0.1-SNAPSHOT.jar
```

### Usage

The Application has two endpoints
```
GET     /details/person
        query params:
            name: String        Name of the person to search
            force: boolean      Force fetch and update from all data sources (default false)
        Description:
            Runs all the datasources, knowledge engines and data sinks and returns all the
            raw data and interpreted data as json
        Response Format: 
            {
                "key": "<Requested query string>"
                "data": [Array of raw data]
                "info": [Array of interpreted information]
            }


POST    /details/multiple
        body: JSON array of names
        Description:
            Runs all the datasources, knowledge engines and data sinks for each name in 
            batches (default 100). Streams the completed names and count of found raw data sources.
        Response Format:
            1       Jeff Bezos          2
            2       Stephen Hawkings    1
            ...
            ...
            <index> <Name>              <Number of sources>
```

### Test
```sh
$ # replace people0.json by people1.json for 1000 people dataset
$ curl -vX POST http://localhost:8080/details/multiple -d @people0.json --header "Content-Type: application/json"
```

## Development

At the core of InfoDB is interface IDataEndpoint. Two types of data endpoints are there : IDataSource and IDataSink
IDataSource : For getting information like `cache, database, crawlers, scrapers and api`
IDataSink: Endpoints which take in data like `cache, database`

Each of these Endpoints are executed in sets of their order for each query.
```
MySimpleCache:      Order 1         Source, Sink
NosqlDB:            Order 2         Source, Sink
GoogleScraper:      Order 3         Source
WikipediaScraper:   Order 3         Source
GithubAPI:          Order 4         Source
```

if information about the query is not found at components of an order the all data sources with next order are run until the end.
After the information is found at an order the all the knowledge engines are run on the collected information.
After that the data collected from the data sources and knowledge engines is collected and supplied to the data sinks before the layer the data was found. For example if the data was found at the DB level only the cache data sink is executed and If the data was found from the scraper the database sink and cache sinks are executed.

In order to create a data sink or data source
- Make a new class for the component
- extend `com.mdakram28.infodb.datamanager.IDataSource` and `com.mdakram28.infodb.datamanager.IDataSink`
- anotate with @Component
- set the order of the endpoint by @Order(value=<Order of component>) on the class
- Override the getType() function: return com.mdakram28.infodb.datamanager.IDataEndpoint.TYPE.{DATABASE | CACHE | SCRAPER | API}
- Override getName() function: return the name of the endpoint
- For IDataSource implement the fetchInfo(String key) function: Return List<Info> i.e. the list of info found by the component or null if not found
- For IDataSink implement the store(InfoList infoList) function: Store the information from infoList.getData().

#### Example - MySimpleCache

```java
package com.mdakram28.infodb.tools.simplecache;

@Component
@Order(value=1)
public class SimpleCache implements IDataSource, IDataSink{

    // Local memory store
	Map<String, Info> cache = new HashMap<>();

	@Override
	public String getType() {
		return TYPE.CACHE;
	}

	@Override
	public List<Info> fetchInfo(String key) throws Exception {
		key = key.toLowerCase().replaceAll("\\s", "");
		List<Info> ret = new ArrayList<>();
		for(String title : cache.keySet()) {
		    // Match the query by stored titles
			if(title.indexOf(key) >= 0) {
				ret.add(cache.get(title));
			}
		}
		return ret;
	}

	@Override
	public void storeInfo(InfoList obj) throws Exception {
		for(Info info : obj.getData()) {
		    // Store all the found information from different data sources in cache
			cache.put(info.title.toLowerCase().replaceAll("\\s", "") + " " + info.getToolName(), info);
		}
	}

	@Override
	public String getName() {
		return "Simple Cache";
	}
	
}

```
