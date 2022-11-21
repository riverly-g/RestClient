## Rest Client
Rest API에 요청 보내고 응답 받을 수 있는 클라이언트입니다.

## 사용법
```java
RestClient restClient = new RestClient();
RestRequest request = restClient.request()
    .url("localhost:8080/test")
    .method(HttpMethod.GET.getMethod())
    .build();
RestResponse response = restClient.call(request);
```

RestClient 객체를 생성합니다.  

```java
RestClient restClient = new RestClient();
```
  
RestRequest를 생성합니다.
요청 주소와 메소드 등을 설정합니다.

```java
RestRequest request = restClient.request()
    .url("localhost:8080/test")
    .method(HttpMethod.GET.getMethod())
    .build();
```

RestReuqest를 바탕으로 요청을 보내고 응답을 받습니다.

```java
RestResponse response = restClient.call(request);

or

RestResponse response = restClient.request().url("localhost:8080/test").get();
```

RestResponse 에서 응답 결과를 받아올 수 있습니다.

```java
Map<String, Object> data = response.data();
// {test=ok}
```

### BaseUrl
RestClient에 BaseUrl을 설정하면 도메인을 생략하고 요청을 보낼 수 있습니다.

```java
RestClient restClient = new RestClient();
restClient.setBaseUrl("localhost:8080");

RestResponse response = restClient.request().url("test").get();
Map<String, Object> data = response.data();
```

### 비동기 요청
RestClient에 asyncCall을 이용하여 비동기 요청을 할 수 있습니다.
비동기 요청은 Future를 반환합니다.

```java
RestClient restClient = new RestClient();
RestRequest request = restClient.request()
    .url("localhost:8080/test")
    .method(HttpMethod.GET.getMethod())
    .build();
    
Future<RestResponse> responseFuture = restClient.asyncCall(request);
```
