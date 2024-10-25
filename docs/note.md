* .env file on the root folder
VUE_APP_SENDGRID_API_KEY=____
VUE_APP_ADMIN_EMAIL=nguyentien.jobs@gmail.com


* link .env with front-end, so every change on the .env on the root folder
* will be synced to the .env file on the frontend folder
* make sure you are IN frontend/ directly
ln -s ../.env .env
ln -s ../.env .env.development.local
ln -s ../.env .env.test.local
ln -s ../.env .env.production.local
* check the link .env
ls -l .env
* you will see something like this
lrwxrwxrwx 1 user user 7 Oct 25 12:00 frontend/.env -> ../.env

