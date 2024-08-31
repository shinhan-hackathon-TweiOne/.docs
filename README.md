# ⏰ShinhanTime⏰
## 💱송금의 문화를 바꿀 시간 : 신한 타임

**UWB/BLE**만 있다면 누구나 사용할 수 있는 차세대 간편송금플랫폼❗❗❗ <br> <br>

### ✅ 기획 배경

    ✓ 간편 송금 서비스의 수요 급증
    ✓ 중고 거래와 플리마켓의 활성화
    ✓ 플랫폼 미사용시, 개인간 거래의 복잡성 증가와 개인정보 유출 가능성 
<br>

### 📌핵심 기능
**정확성 [ UWB  및 BLE 기반 근거리 송금 ]** <br>

    - UWB 기술을 통해 송금 대상자와 수신자가 일정 거리 내에 있을 때 자동 인식
    - UWB의 정밀한 위치 파악 + BLE의 광범위한 호환성 -> 송금 대상자에게 안전한 송금 수행
    - UWB 및 BLE 통신 과정에서 모든 데이터는 고급 암호화 기술로 보호

**신뢰성 [ 자이로센서를 통한 간편 송금 및 신뢰 보장 ]** <br>

    - 자이로 센서를 통해 불필요한 오작동을 방지하고 별도의 조작 없이 송금 절차 자동 시작
    - 물리적 접촉을 통한 추가 인증 단계로, 잘못된 송금을 방지하여 사용자 신뢰성 강화
    - 생체인식(지문, 얼굴 인식) 또는 PIN 번호를 통한 이중 인증 절차로 보안성 강화

**특화성 [ 판매자 대상 간이 카드 단말기 기능 ]** <br>

    - 판매자의 핸드폰이 간이 키오스크처럼 기능하여, 상품 정보 입력/가격 설정/결제 요청 등을 관리

<br>

### 💬 아이디어 차별성
**Easy Safety 간편 송금**

    - UWB와 BLE 기술을 이용한 근거리 통신을 통해, 사용자는 복잡한 계좌 정보 입력 없이도 자동으로 송금 가능
    - 자이로센서를 활용한 간단한 추가 인증 단계로 송금의 신뢰성을 높이며, 이중 인증과 고급 암호화 기술로 데이터 보안을 보장

**Everywhere Easy 간이 키오스크 생성**

    - 판매자 중심의 특화된 기능 제공을 통해 플리 마켓과 같은 환경에서 판매 효율 극대화
    - 판매자 모드를 통해 상품 정보 입력/가격 설정 등 간이 키오스크처럼, 결제 요청과 같이 간이 카드 단말기처럼 동작 가능

<br>

### 💡 기대 효과
    - 혁신적인 기술과 플로우 도입으로 송금 신뢰성 확보
    - 플랫폼에 구애받지 않는 통합 서비스
    - 다각화된 기능 제공으로 고객 점유율 확보

<br>

## 📝개발 정보📝
### 🛠️ 기술 스택
####  Frontend
<img src="https://img.shields.io/badge/Android-34A853?style=for-the-badge&logo=android&logoColor=white">

####  Backend
<img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
<img src="https://img.shields.io/badge/redis-FF4438?style=for-the-badge&logo=redis&logoColor=white">
<img src="https://img.shields.io/badge/amazons3-569A31?style=for-the-badge&logo=amazons3&logoColor=white">
<img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">
<img src="https://img.shields.io/badge/solidity-363636?style=for-the-badge&logo=solidity&logoColor=white">
<img src="https://img.shields.io/badge/ethereum-3C3C3D?style=for-the-badge&logo=ethereum&logoColor=white">

####  Infra
<img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white"> <img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white">
<img src="https://img.shields.io/badge/nginx-009639?style=for-the-badge&logo=nginx&logoColor=white">
<img src="https://img.shields.io/badge/githubactions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white">
<img src="https://img.shields.io/badge/AWS-FF9900?style=for-the-badge&logo=amazonwebservices&logoColor=white">
<img src="https://img.shields.io/badge/amazons3-569A31?style=for-the-badge&logo=amazons3&logoColor=white">

#### 협업
<img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"> <img src="https://img.shields.io/badge/mattermost-0058CC?style=for-the-badge&logo=mattermost&logoColor=white">
<img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=white">

### 💾 ERD
![erd](https://github.com/user-attachments/assets/91b3e006-ee1e-4803-8369-b1eec6271474)

## 🐣 빌드 및 배포

### 블록 체인

**사용 툴 및 버전**

    - go-ethereum(geth) 1.13.15 => 1.14.x 버전 업데이트로 프라이빗 네트워크 생성이 까다로워짐.
    - solidity compiler(solc) 0.8.19 => geth 버전과 통일.
    - web3j 1.6.1

**blockchain 경로 생성**

``` Bash
mkdir blockchain
```

**관리자 계좌 생성**

``` Bash
geth --datadir {사용자 블록체인 경로} account new
// password 입력 필요.
```

**제네시스 블록 파일 생성**

``` json
{
  "config": {
    "chainId": 1234,
    "homesteadBlock": 0,
    "eip150Block": 0,
    "eip155Block": 0,
    "eip158Block": 0,
    "byzantiumBlock": 0,
    "constantinopleBlock": 0,
    "petersburgBlock": 0,
    "istanbulBlock": 0,
    "clique": {
      "period": 5,
      "epoch": 30000
    }
  },
  "difficulty": "1",
  "gasLimit": "8000000",
  "alloc": {
    "사용자 관리자 계좌 주소": { "balance": "1000000000000000000000000" }
  },
  "extradata": "0x0000000000000000000000000000000000000000000000000000000000000000{사용자 관리자 계좌 주소}00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000"
}

```

**제네시스 블록으로 프라이빗 네트워트 초기화**

``` Bash
geth --datadir {사용자 블록체인 경로} init {제네시스 블록 경로}
```

**프라이빗 네트워크 실행**

``` Bash
geth --datadir {사용자 블록체인 경로} --networkid 1234 --nodiscover --unlock {사용자 관리자 계좌 주소} --password {사용자 관리자 계좌 비밀번호} --verbosity 3 console --http --http.addr "127.0.0.1" --http.port "8545" --http.api personal,eth,net,web3,miner --http.corsdomain "*" --allow-insecure-unlock
```

**관리자 계정으로 블록 마이닝**

``` Bash
miner.setEtherbase(eth.accounts[0])
miner.start()
eth.blockNumber // 블록이 추가 됨을 롹인
```

### AWS
<img width="631" alt="AWS_set" src="https://github.com/user-attachments/assets/c032fc2d-0e9c-49bd-8067-c172a39aba20">

**GitHub Action을 통한 빌드 및 배포 자동화**

```
name: Java CI with Gradle

on:
  push:
    branches: [ "develop", "main" ]
  pull_request:
    branches: [ "main" ]
jobs:
  build:
    runs-on: ubuntu-latest
    permissions: write-all
    steps:
    - uses: actions/checkout@v4
    
    ## jdk setting
    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
        
    ## gralew 실행 권한 부여  
    - name: Grant execute permission for gradlew
      run: chmod +x ./gradlew
      
    - name: Setup Gradle
      uses: gradle/actions/setup-gradle@af1da67850ed9a4cedd57bfd976089dd991e2582 # v4.0.0
    
    ## 빌드 
    - name: Build with Gradle
      run: ./gradlew build -x test
      
    ## Docker hub login & build & push
    - name: Docker hub login
      uses: docker/login-action@v3
      with:
        username: ${{ secrets.DOCKER_ID }}
        password: ${{ secrets.DOCKER_PASSWORD }}
        
    - name: Docker image build
      run: |
        docker build -f Dockerfile -t ${{ secrets.DOCKER_USERNAME }}/${{ secrets.DOCKER_REPO }} .
        docker push ${{ secrets.DOCKER_USERNAME }}/${{ secrets.DOCKER_REPO }}

    ## 실행되고 있는 IP 가져와서 EC2 보안 그룹에 추가하여 접근하기
    - name: Get Public IP
      id: ip
      uses: haythem/public-ip@v1.3

    - name: Configure AWS credentials
      uses: aws-actions/configure-aws-credentials@v4
      with:
        aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
        aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
        aws-region: 'ap-northeast-2'

    - name: Add GitHub Actions IP
      run: |
        aws ec2 authorize-security-group-ingress \
            --group-id ${{ secrets.SECURITY_GROUP_ID }} \
            --protocol tcp \
            --port 22 \
            --cidr ${{ steps.ip.outputs.ipv4 }}/32

    ################## CD ##################

    ## AWS EC2 접속
    - name: Deploy to Dev
      uses: appleboy/ssh-action@master
      with:
        host: "${{ secrets.GDP_HOST }}"
        username: "${{ secrets.GDP_USERNAME }}"
        key: "${{ secrets.GDP_KEY }}"
        script: |
            sudo docker pull ${{ secrets.DOCKER_USERNAME }}/${{ secrets.DOCKER_REPO }}
            sudo docker-compose down
            sudo docker-compose -f "docker-compose.yml" up -d --build
    ## 실행
    ## 사용한 IP 보안 그룹 삭제
    - name: Remove GitHub Actions IP
      run: |
        aws ec2 revoke-security-group-ingress \
            --group-id ${{ secrets.SECURITY_GROUP_ID }} \
            --protocol tcp \
            --port 22 \
            --cidr ${{ steps.ip.outputs.ipv4 }}/32
```

