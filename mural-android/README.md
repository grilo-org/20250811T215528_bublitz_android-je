# Como gerar release do aplicativo Mural Eletrônico
Antes de gerar a release, não esquecer de mudar o "versionCode" do arquivo build.gradle(Module: app) do projeto.

1. No Android Studio, acesse o menu Build -> Generated Signed APK;
2. Em Key store path, adicione o key store (O key store deste projeto está na pasta key-store);
3. Em Key store password, adicione a senha. A senha está armazenada no servidor de arquivo \\mt11\sad\Projetos\mobile\release-no-android-studio.txt;
4. Em Key alias, adicione "tre-mt";
5. Em Key password, adicione a mesma senha inserida em "Key store password";
6. Clique em next. Em Build Type, escolha "release";
7. Clique em finish. A APK vai ser gerada e salva na pasta app do projeto, com o nome
app-release.apk;

obs: Os dados de acesso da loja Google Play está disponivel em \\mt11\sad\Projetos\mobile\acesso-para-publicar-googleplay.txt
