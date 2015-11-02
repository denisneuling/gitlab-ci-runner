gitlab-ci-runner
================

This is a java implementation of the [official gitlab-ci-runner](https://gitlab.com/gitlab-org/gitlab-ci-multi-runner)

# Prerequisites

* [A running GitlabCI instance](https://about.gitlab.com/gitlab-ci/)
* [Docker](https://www.docker.com/)
* [Java](https://www.java.com/)

# Installation

1. Get the binary

```bash
    $ curl -L https://github.com/denisneuling/gitlab-ci-runner/releases/download/1.0.0-rc1/gitlab-ci-runner-1.0.0-rc1-executable.jar -o gitlab-ci-runner
```

2. Set executable bit

```bash
    $ chmod +x gitlab-ci-runner
```

3. Run

```bash
    $ ./gitlab-ci-runner register
    $ ./gitlab-ci-runner start
```

# Configuration parameters

***TODO***

# TODO

* Support [***.gitlab-ci.yml#services***](http://doc.gitlab.com/ci/yaml/README.html)
