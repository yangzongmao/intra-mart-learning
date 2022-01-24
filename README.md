# intra-mart-learning

## 関連資料  

* intra-mart　体験版ダウンロードリンク  
  <http://www.intra-mart.jp/download/try/trylist_contents_previous.html>
  
* intra-mart WebPlatForm/AppFramework Ver 7.2  
  <http://www.intra-mart.jp/download/try/iwp72_installer.zip>   
  
* intra-mart WebPlatForm/AppFramework Ver 7.2 セットアップガイド   
  <http://www.intra-mart.jp/download/product/v72_doc/iwp_iaf/install/iwp_setup_guide_v72.pdf>   
  
* 開発ツール(eBuilder)   
  <http://www.intra-mart.jp/download/product/ebuilder/eBuilder8-win32.win32.x86_64.zip>   
  
* 関連ドキュメント（ガイドなど）ダウンロードリンク   
  <http://www.intra-mart.jp/download/product/index_v72.html>  
  
* resin-4.0.49  
  <http://www.caucho.com/download/resin-4.0.49.zip>  
  
* postgressql  
  <http://get.enterprisedb.com/postgresql/postgresql-9.5.5-1-windows-x64.exe>  
  
* Apache Cassandra 1.1.12  
  <http://archive.apache.org/dist/cassandra/1.1.12/apache-cassandra-1.1.12-bin.tar.gz>
  
  ap-northeast-1_4EpY9W73K

2r3r02vgs2b9vc2i897s8iqm7v

// Amazon Cognito 認証情報プロバイダーを初期化します
AWS.config.region = 'ap-northeast-1'; // リージョン
AWS.config.credentials = new AWS.CognitoIdentityCredentials({
    IdentityPoolId: 'ap-northeast-1:c48d5939-ea91-46d5-b5c6-ec5aec5a27dd',
});
