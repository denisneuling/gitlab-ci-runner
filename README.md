gitlab-ci-runner
================

This is a java implementation of the [official gitlab-ci-runner](https://gitlab.com/gitlab-org/gitlab-ci-multi-runner)

# Prerequisites

* Java
* Maven

# Installation

1. Get the code

```bash
    $ git clone git@lab.metapatrol.com:metapatrol/gitlab-ci-runner.git
```

2. Compile

```bash
    $ cd gitlab-ci-runner && mvn clean install
```

3. Run

```bash
    $ java -jar target/gitlab-ci-runner-*-executable.jar register
    $ java -jar target/gitlab-ci-runner-*-executable.jar start
```
