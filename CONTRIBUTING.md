# Contributing to Kabassu

Are you interested in contributing to Kabassu? Great!
Every contribution is welcome: be it a pull request, a new documentation or update to existing ones, helping others with their issues or voicing your opinion in discussed issues!

## Bugs

If you encounter a bug when using Kabassu:, 

1. Check the existing [issues](https://github.com/kabassu/kabassu/issues) - perhaps someone already had the same problem. If there is one, don't hesitate to add a comment with additional information about yout context!
2. If there are not issues addressing your problem, report a new one, according to the template.

## Feature requests/proposals

Perhaps you have a cool idea on how to improve Kabassu? Raise an issue and describe your proposal, including why you think it would make a great addition to the tool.

## Contributing code

Maybe not only you have a cool idea but felt like moving it right away into th code? Or you saw an issue labeled `help wanted` and felt that you'd give a try? Even more awesome!

Fork the repository, create a branch, hack away your improvement and submit a pull request, using the template on GitHub!

There are few things you need to keep in mind as well.

### Commit descriptions

Describe your changes in the pull request. Also, make your commit messages speak for themselves (you can read more about that [here](https://chris.beams.io/posts/git-commit/).

### Changelog
Please remember to update [Changelog](CHANGELOG.md) while changing stuff in Kabassu.
Add new entry in the `Unreleased` section.
Use the following convention: convention `- [PR-ABC](https://github.com/Kabassu/${repository}/pull/ABC) - short description`.

The above information will also be used in release notes for given version.

#### API-breaking changes
If there's a need for the users to somehow modify their existing Kabassu setups, provide necessary migration steps under your change. 

### Testing

All the changes to the codebase should be reflected by appropriate automated checks. These can be either unit/integration/E2E tests - if you're not sure where to put them, contact us!
 
As for the naming convention, please use #3 from [this list](https://dzone.com/articles/7-popular-unit-test-naming), i.e. `test[Feature being tested]:`. Example tests:

```
testIsNotAnAdultIfAgeLessThan18
testFailToWithdrawMoneyIfAccountIsInvalid
testStudentIsNotAdmittedIfMandatoryFieldsAreMissing
```

Also, use JUnit 5's `@DisplayName` for providing the necessary context and additional info.

### Coding conventions

Below is short list of things that will help us keep Kabassu quality and accept pull requests:
- Follow Google Style Guide code formatting, particularly set your IDE `tab size`/`ident` to 2 spaces and `continuation ident` to 4 spaces.
  - [Google Style Guide for Eclipse](https://raw.githubusercontent.com/google/styleguide/gh-pages/eclipse-java-google-style.xml)
  - [Google Style Guide for IntelliJ](https://raw.githubusercontent.com/google/styleguide/gh-pages/intellij-java-google-style.xml)
- when logging use proper levels: `INFO` and `WARNING` should log only very important messages,
- your code should pass SonarCloud quality gates: [link](https://sonarcloud.io/dashboard?id=io.kabassu).

### Documentation
Providing a neat description how to use your feature or what does your PR change in the existing behavior is very important for Kabassu users.
Create appropriate documentation in the docs site repository: [link](https://github.com/Kabassu/kabassu.github.io).

In addition to the above, please make sure all public APIs have Javadocs.

