1) npm install firebase
2) firebase --version
3) firebase init  
    - init firebase project in root folder
    - select hosting
	    - choose existing Firebase project
        - set `dist` as the public directory.
	    - select Yes for single-page app
4) npm run build  
    build to dist
5) firebase deploy
    deploy /dist