[![Build Status](https://travis-ci.org/RomanMozhaev/job4j_parser.svg?branch=master)](https://travis-ci.org/RomanMozhaev/job4j_parser)
[![codecov](https://codecov.io/gh/RomanMozhaev/job4j_parser/branch/master/graph/badge.svg)](https://codecov.io/gh/RomanMozhaev/job4j_parser)
# job4j
# Job Offering Parser
The Job Offering Parser application parses the job offering from the web-site "www.sql.ru" for Java Developers.
The application uses the quartz-scheduler, which could be adjusted for starting of the parsing by a cron.time parameter.
The application starts parsing from the current year beginning, if it is the first application launch; Otherwise from the last launching time.
The offers with equals names are considered as equals too. All parsed job offering are saved into the database.
The file with properties (see appParser.properties) must be passed as argument for the application launching.

The following technologies were used:
1) Quartz-scheduler
2) JDBC
3) Liquibase
4) PostgreSQL
6) SLF4J, LOG4J
7) JUnit
