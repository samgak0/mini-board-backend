#  Git 커밋 메시지 컨벤션

---

# 1. 커밋 메시지 형식
커밋 메시지는 다음과 같은 구성 요소를 따릅니다:

1. **타입(Type)**: 작업의 종류를 나타내며, 소문자로 작성합니다.
2. **제목(Subject)**: 변경 사항에 대한 간단하고 명령형으로 작성된 설명입니다.
3. **본문(Body, 선택 사항)**: 구체적인 변경 사항에 대한 설명입니다. 필요할 때만 작성합니다.
4. **푸터(Footer, 선택 사항)**: 참고할 이슈 번호나 추가 정보를 포함합니다.

---

# 2. 타입(Type)
다양한 작업 유형을 구분하기 위해 타입을 사용합니다.

- **기능**: 새로운 기능 추가 (`feat`)
- **고침**: 버그 수정 (`fix`)
- **수정**: 동작 중인 기능을 개선하거나 최신의 버전으로 업데이트 (`update`)
- **추가**: 새로운 파일, 기능, 또는 코드를 프로젝트에 추가 (`add`)
- **개선**: 동작 중인 코드를 기능의 변경 없이 성능을 개선하거나 유지 보수성을 높힘 (`improve`)
- **문서**: 문서 관련 변경 (`docs`)
- **스타일**: 코드 스타일 변경, 포맷팅, 주석 수정 등 (`style`)
- **리팩토링**: 코드 리팩토링, 기능 변경 없이 구조 개선 (`refactor`)
- **테스트**: 테스트 코드 추가 또는 수정 (`test`)
- **빌드**: 빌드 시스템 또는 외부 종속성 관련 작업 (`build`)
- **성능**: 성능을 개선하는 변경 (`perf`)
- **환경설정**: 프로젝트 환경설정 관련 작업 (`chore`)

---

# 3. 커밋 메시지 예시
## 3.1. 타입과 제목만 사용하는 경우
```
기능: 사용자 로그인 기능 추가
수정: 비밀번호 해시 오류 수정
문서: README에 사용법 추가
스타일: 코드 정렬 및 주석 수정
```

## 3.2. 타입, 제목, 본문 사용하는 경우
```
기능: 사용자 로그인 기능 추가
- JWT 토큰 발급 기능 구현
- 로그인 시 유효성 검사 추가
- 기존 회원가입 기능과 연동 완료
```

```
수정: 비밀번호 해시 오류 수정
- 비밀번호 해시 로직 오류 수정
- 잘못된 입력 처리 로직 개선
```

## 3.3. 이슈와 연관이 있는 경우
```
환경설정: CI/CD 설정 파일 추가
- GitHub Actions 사용해 자동 빌드 설정
- 코드 변경 시 자동 테스트 실행

참고: #123
```

---

# 4. 작성 규칙
1. **명령형 사용**: 커밋 메시지는 명령형으로 작성합니다. 예를 들어, "추가함", "수정함" 대신 "추가", "수정"을 사용합니다.
2. **길이 제한**: 제목은 50자 이내로 작성합니다. 본문은 필요한 경우에만 작성하며, 각 줄은 72자를 넘지 않도록 합니다.
3. **본문 작성**: 변경 사항을 설명할 때 "무엇을" 변경했는지와 "왜" 변경했는지를 포함합니다.