# ❤ GongGurumi ❤

//배너자리


- SSAFY 1학기 관통프로젝트 **Team BuyTogether**
- 프로젝트 기간 : `2022.10.29` ~ `2021.11.25`


<br>

# :green_book:​Contents

[:one:​ Specification](#one-specification)<br>
[:two:​ Package Structure](#two-package-structure)<br>
[:three:​ Git & Naming Rule](#three-package-structure)<br>
[:four:​ 핵심 기능 구현 방법 설명](#four-핵심-기능-구현-방법-설명)<br>
[:five:​ Contributor](#five-contributor)<br>




<br>

## ​:one:​ Specification

<table class="tg">
<tbody>
  <tr>
    <td><b>Architecture</b></td>
    <td>MVVM</td>
  </tr>
<tr>
    <td><b>Design Pattern</b></td>
    <td>Singleton</td>
</tr>
<tr>
    <td><b>Jetpack Components</b></td>
    <td>DataBinding, LiveData, ViewModel, Lifecycle</td>
</tr>
<tr>
    <td><b>Dependency Injection</b></td>
    <td>Koin</td>
</tr>
<tr>
    <td><b>Network</b></td>
    <td>Retrofit2, OkHttp</td>
</tr>
<tr>
    <td><b>Strategy</b></td>
<td></td>
</tr>

<tr>
    <td><b>Third Party Library</b></td>
    <td>Glide, Spinkit, TedPermission, Kakao/Google API</td>

</tr>
<tr>
    <td><b>Other Tool</b></td>
<td>Notion, Trello</td>
</tr>
</tbody>
</table>

<br>

<br>


## :two:​ Package Structure

```
📦 com.buy.together
 ┣ 📂 data
 ┃ ┗ 📂 local
 ┃ ┗ 📂 model
 ┃ ┃ ┣ 📂 domain
 ┃ ┃ ┗ 📂 network
 ┃ ┗ 📂 repository
 ┣ 📂 network
 ┣ 📂 ui
 ┃ ┗ 📂 adapter
 ┃ ┗ 📂 base
 ┃ ┗ 📂 view
 ┃ ┃ ┣ 📂 alarm
 ┃ ┃ ┣ 📂 board
 ┃ ┃ ┣ 📂 chat
 ┃ ┃ ┣ 📂 main
 ┃ ┃ ┣ 📂 map
 ┃ ┃ ┣ 📂 mypage
 ┃ ┃ ┣ 📂 splash
 ┃ ┃ ┗ 📂 user
 ┃ ┗ 📂 viewmodel
 ┣ 📂 util
 ┃ ┗ 📂 extension
 ┗ 📜 Application.kt
```



<br>

## :three:​ Git & Naming Rule
### [Git Rules]
- **전체 개요**
   - **Title** :  은 **"[타입] 설명"** 형식으로 작성
   - **Content** : 추가적인 설명이 필요할 경우 작성
- **타입 작성 방법**
: 타입은 괄호 내에 작성하며, 대문자로 표기
  - **PROJECT** : 프로젝트 리소스 관련 커밋 (Ex. src, gradle)
  - **README** : 리드미 관련 커밋
  - **FEATURE** : 개발 기능 구현 관련 커밋
  - **FIX** : 버그, 에러 관련 커밋
- **설명 작성 방법**
: 설명은 괄호 뒤에 작성하며, 스칼라 표기법으로 표기.
  - **README 타입** : Update + (내용)으로 커밋
  - **Feature 타입** :  패키지명 - 세부 기능명

### [Naming Rules]
- **Java/Kotlin**
  - **file 네이밍**
     - 표기법 : 파스칼 표기
     - 표기 형식 : 패키지명 + (____) + 컴포넌트명
     - Ex. MainFragment, MainNaviFragment
  - **함수 네이밍**
     - 표기법  : 카멜 표기
     - 표기 형식 : 타입/행위명(REST Rule) + (_____)
     - Ex. getUser, deleteUser
  - **변수 네이밍**
     - 표기법 : 카멜 표기
     - 표기 형식 : (_____) + 타입/클래스명
     - Ex. userList, userDto
- **XML**
  - **file 네이밍**
     - 표기법 : 스네이크 
     - 표기 형식 : 컴포넌트명 + 패키지명 + (____)
     - Ex. fragment_main, fragment_main_navi
  - **변수 네이밍**
     - 표기법 : 스네이크
     - 표기 형식 : 타입/클래스약자명 + (______)
     - Ex. tv_user_name, btn_sumbit, rv_user


<br>


## :four:​ 핵심 기능 구현 방법 설명

```
👉 WIKI에 핵심 기능 구현 코드 및 방법 정리
```

[1. 스플래시](https://github.com/TeamMyDaily/4most-Android/wiki/5.1.-%EC%8A%A4%ED%94%8C%EB%9E%98%EC%8B%9C)

[2. 로그인](https://github.com/TeamMyDaily/4most-Android/wiki/5.2.-%EB%A1%9C%EA%B7%B8%EC%9D%B8)

[3. 회원가입](https://github.com/TeamMyDaily/4most-Android/wiki/5.3.-%ED%9A%8C%EC%9B%90%EA%B0%80%EC%9E%85)

[4. 글 작성](https://github.com/TeamMyDaily/4most-Android/wiki/5.4.-%ED%82%A4%EC%9B%8C%EB%93%9C-%EC%84%A0%ED%83%9D)

[5. 글 확인](https://github.com/TeamMyDaily/4most-Android/wiki/5.5.-%ED%82%A4%EC%9B%8C%EB%93%9C-%EC%B4%88%EA%B8%B0-%EC%84%A4%EC%A0%95)

[6. 지도](https://github.com/TeamMyDaily/4most-Android/wiki/5.5.-%ED%82%A4%EC%9B%8C%EB%93%9C-%EC%B4%88%EA%B8%B0-%EC%84%A4%EC%A0%95)

[7. 채팅](https://github.com/TeamMyDaily/4most-Android/wiki/5.6.-%EA%B8%B0%EB%A1%9D)

[8. 알람](https://github.com/TeamMyDaily/4most-Android/wiki/5.7.-%EB%AA%A9%ED%91%9C)

[9. 마이페이지](https://github.com/TeamMyDaily/4most-Android/wiki/5.9.-%EB%A7%88%EC%9D%B4%ED%8E%98%EC%9D%B4%EC%A7%80)




<br>

## :five:​ Contributor

```
👉 팀원 소개와 역할 분담
```

<table class="tg">
<tbody>
    <tr>
        <td>이지윤</td>
        <td>이아름</td>
    </tr>
    <tr>
        <td><a href="https://github.com/jiy00nLee">@jiy00nLee</a></td>
        <td><a href="https://github.com/Lee-Areum">@Lee-Areum</a></td>
    </tr>
    <tr>
        <td><img src="/wiki/contributor/4z7l.png" width="300px"/></td>
        <td><img src="/wiki/contributor/mdb1217.jpeg"  width="300px"/></td>
    </tr>
    <tr>
        <td></td>
        <td></td>
    </tr>
</tbody>
</table>

