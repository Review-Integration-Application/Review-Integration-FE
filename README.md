# 🍽️ 쩝쩝리뷰 (JjeopJjeop Review)
> **지도 플랫폼별 리뷰를 하나로, AI 기반 통합 리뷰 제공 애플리케이션**

## 📖 프로젝트 소개 (Introduction)
**쩝쩝리뷰**는 네이버 지도와 카카오 맵 등 여러 플랫폼에 분산된 식당 리뷰를 **하나의 앱에서 통합하여 보여주는 서비스**입니다. 
단순한 리뷰 나열을 넘어, **Google Gemini API**를 활용하여 방대한 리뷰를 핵심만 요약하고 긍정/부정 분석을 제공하여 사용자의 신속한 의사결정을 돕습니다.

### ❓ 기획 의도
* **정보의 분산:** 사용자가 리뷰를 확인하기 위해 여러 지도 앱을 오가야 하는 불편함 해소 
* **탐색의 비효율:** 수많은 리뷰를 일일이 읽는 피로도(User Fatigue) 감소
* **객관적 정보:** 플랫폼별 편향성 없는 균형 잡힌 시각 제공

---

## ✨ 주요 기능 (Key Features)

### 1. 리뷰 데이터 통합 (Review Aggregation)
* 네이버 지도와 카카오 맵의 리뷰 데이터를 수집하여 한곳에서 제공합니다. 
* 식당별 통합 별점 및 전체 리뷰 리스트를 확인할 수 있습니다.

### 2. AI 기반 리뷰 요약 (AI Summarization using Gemini)
* **한 줄 요약:** 수백 개의 리뷰를 분석하여 식당의 특징을 한 문장으로 요약합니다. 
* **자동 해시태그:** `#곱창전골맛집`, `#불쇼퍼포먼스` 등 핵심 키워드를 태그로 추출합니다.
* **장단점 분석:** 고객이 만족한 포인트(맛, 친절 등)와 불만 포인트를 명확하게 구분하여 요약합니다. 

### 3. 감정 분석 시각화 (Sentiment Analysis)
* [cite_start]전체 리뷰 데이터를 분석하여 긍정/부정 비율을 직관적인 그래프로 제공합니다.

---

## 🛠 기술 스택 (Tech Stack)

| 구분 | 기술 (Technology) | 설명 |
| :-- | :-- | :-- |
| **Mobile App** | ![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=kotlin&logoColor=white) ![Android Studio](https://img.shields.io/badge/Android%20Studio-3DDC84?style=flat&logo=android-studio&logoColor=white) | 안드로이드 네이티브 앱 개발 |
| **Backend** | ![FastAPI](https://img.shields.io/badge/FastAPI-009688?style=flat&logo=fastapi&logoColor=white) ![Python](https://img.shields.io/badge/Python-3776AB?style=flat&logo=python&logoColor=white) | REST API 서버 구축 및 데이터 처리  |
| **AI / ML** | ![Gemini](https://img.shields.io/badge/Google%20Gemini-8E75B2?style=flat&logo=google&logoColor=white) | 리뷰 요약, 키워드 추출, 감정 분석 API 활용 |
| **Data** | **Selenium / Crawling** | 네이버/카카오 지도 데이터 수집 및 전처리 |
| **Database** | **DB** | 수집된 식당 및 리뷰 데이터 저장 |

---

## 🏗 시스템 구성도 (System Architecture)

1.  **데이터 수집기(Python):** 네이버/카카오 지도에서 식당 정보 및 리뷰 크롤링 [cite: 35, 42]
2.  **데이터베이스:** 수집된 원본 데이터 저장
3.  **백엔드(FastAPI):** 앱 요청 처리 및 Gemini API와 통신하여 요약 데이터 생성 [cite: 38-40]
4.  **안드로이드 앱(Kotlin):** 사용자에게 통합된 정보와 시각화된 데이터 제공 [cite: 48]

---

### 2. AI 상세 요약 및 리뷰 (AI Summary & Integration)
| 통합 정보 & 태그 | AI 장단점 요약 | 플랫폼별 리뷰 통합 |
| :---: | :---: | :---: |
| 네이버/카카오 별점 통합<br>및 핵심 키워드(#) 제공 | **Gemini Pro**를 활용한<br>고객 만족/불만 포인트 요약 | 여러 플랫폼의 리뷰를<br>한 화면에서 확인 |
