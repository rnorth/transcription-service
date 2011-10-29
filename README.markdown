## Transcription Service

This (non-SOAP) web service provides basic Japanese text to HTML Ruby conversion.

The service is a facade on top of the mecab tagging utility, which must be installed on the server and executable on the PATH. The service performs further operations on the tagged text:

 * identifying which pronunciation elements should be used for furigana
 * conversion into HTML ruby markup for rendering

## Usage

The service is a Play! Framework application. Once started in the usual manner, it can be used in two ways:

### HTTP GET

`
 $ curl http://localhost:9000/transcription/transcribe?input=お早うございます。
` 
### HTTP POST

`
 $ curl -v -X POST --form input=@some_file_containing_text.txt http://localhost:9000/transcription/transcribe
`