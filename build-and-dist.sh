#!/usr/bin/env bash
#
#-------------------------------------------------------
#
# author: Luciano Brito
#
#-------------------------------------------------------
#
# Creation
#
# Date: 07/08/2021 at 12:00 o'clock
#
#-------------------------------------------------------
#
# Contacts
#
# e-mail: lucianobrito.dev@gmail.com
# github: github.com/lucianobritodev
#
#-------------------------------------------------------
#
# Versions
#
# v1.0.0
#
#-------------------------------------------------------
#
# To run the script run one of the following commands:
#
# ./build-and-dist.sh
#
# or
#
# bash build-and-dist.sh
#
#-------------------------------------------------------

APP_VERSION="$(cat app.version)"
APP_ARCH="amd64"
BUILD_HOME=env-editor_${APP_VERSION}_${APP_ARCH}

criate_files() {
  echo "Criando diretórios..."

  mkdir -p dist/${BUILD_HOME}/DEBIAN
  mkdir -p dist/${BUILD_HOME}/opt/enveditor
  mkdir -p dist/${BUILD_HOME}/usr/share/applications
  mkdir -p dist/${BUILD_HOME}/usr/share/icons

  echo "Copiando arquivos..."
  cp -R target/image/* dist/${BUILD_HOME}/opt/enveditor/
  cp src/main/resources/icons/enveditor.xpm dist/${BUILD_HOME}/usr/share/icons/
  cp -R src/main/resources/icons dist/${BUILD_HOME}/opt/enveditor/

  echo "Criando arquivos..."
  cat <<EOF >dist/${BUILD_HOME}/DEBIAN/postinst
#!/usr/bin/env bash

echo "Gerando link simbólico da aplicação..."

if [ -f '/usr/bin/enveditor' ]; then
  sudo rm -f /usr/bin/enveditor
fi

sudo ln -s /opt/enveditor/enveditor.sh /usr/bin/enveditor

EOF

  cat <<EOF >dist/${BUILD_HOME}/DEBIAN/control
Package: EnvEditor
Version: $(echo "${APP_VERSION}" | sed 's/[a-zA-Z]//')
Architecture: ${APP_ARCH}
Essential: no
Priority: optional
Depends: pkexec
Maintainer: luciano brito
Description: Create, edit and exclude environment variables

EOF

cat <<EOF >dist/${BUILD_HOME}/usr/share/applications/env_editor.desktop
[Desktop Entry]
Name=EnvEditor
Comment=Editor de variáveis de ambiente
Icon=/opt/enveditor/icons/enveditor.png
Exec=enveditor %f
Categories=Utilities;
Type=Application
Terminal=false
StartupNotify=true
Keywords=env;environment;variable

EOF

  cat <<EOF >dist/${BUILD_HOME}/opt/enveditor/enveditor.sh
#!/usr/bin/env bash

if [ -d '/opt/enveditor' ]; then
  pkexec --disable-internal-agent env DISPLAY=$DISPLAY XAUTHORITY=$XAUTHORITY  '/opt/enveditor/bin/EnvEditor' user.home=${HOME}
fi

EOF

  echo "Atribuindo permissões..."
  chmod +x dist/${BUILD_HOME}/DEBIAN/postinst
  chmod +x dist/${BUILD_HOME}/opt/enveditor/enveditor.sh

}

dist_deb_pack() {
  echo "Criando pacote .deb..."
  cd dist
  dpkg-deb --build ./${BUILD_HOME}

  mv ./${BUILD_HOME}/opt/enveditor ./
  rm -Rf ./${BUILD_HOME}/*

  echo "Criando pacote .tar.gz..."
  cp -R ./enveditor/* ./${BUILD_HOME}/
  tar -czvf ${BUILD_HOME}.tar.gz ${BUILD_HOME}
}

clear_output_dir() {
  echo "Limpando build anterior..."
  rm -Rf out dist
}

build() {
  echo "Buildando o projeto..."
  ./mvnw clean javafx:jlink
}

clear_waste() {
  echo "Limpando resíduos..."
  rm -Rf ./${BUILD_HOME} enveditor
}

dist() {
  clear_output_dir
  build

  if [ -d 'target/image' ]; then
    criate_files
    dist_deb_pack
    clear_waste
  fi
}

dist