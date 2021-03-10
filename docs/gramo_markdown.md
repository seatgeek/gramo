## Gramo Markdown

### Simplified RFC
Files *_must_* be UTF-8 encoded to be eligible. Any file extension will work.

*_may_* nest open and close tags within other gramo tags.

*_must_* always have a matching open and close tag as illustrated below. There are
no shorthands.
```^xml
// Good
<gramo::><::gramo>

// Good
<gramo::>
<gramo::><::gramo>
<::gramo>

// Bad
<::gramo>

// Bad
<gramo::>
```
To be continued...
