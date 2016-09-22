# PATH Tracker - An Alexa Skill Kit

This is an Alexa Skill written in Java and hosted on AWS Lambda.
Provides train timings for the [PATH network](http://www.panynj.gov/path/).

# Development
The boilerplate and template for the Alexa Skill has been borrowed from [this project](https://github.com/ardetrick/amazon-alexa-skill-starter-java), it is an excellet project to pick as a starting point for a Java Alexa Skill.
In case you want to understand the basic structure, I would strongly recommend that you check it out before peeking in here.

After that you should check out the Intent Schema and Sample Utterances to understand how the skill works.
To understand the route finding logic, check out the [GTFS specs] (https://developers.google.com/transit/gtfs/)
You can find the source data from the  [PATH website] (http://data.trilliumtransit.com/gtfs/path-nj-us/)

## Libraries
- Guice for dependency injection
- Slfj with Log4j bindings for logging
- JUnit for testing
- Gradle for building
- [Immutables](https://immutables.github.io/) for cleaner POJOs

## Contributions and enhancements
Here are some of the things that I'd like to improve to make this skill more usable.
- Twitter integration to report statuses from @PATHTrain
- Performance improvements in the route finding algorithm (Maybe pre-process data and move it to a SQLite DB do avoid reading from files)
- Support Connections, right now only direct routes are found (Eg if you searched from Newark to Hoboken on a weekday, it'd say no trains. You could however go from Newark to Exchange Pl and then take a train to Hoboken)
