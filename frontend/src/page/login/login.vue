<template>
    <div class="loginContainer">
        <head-top :head-title="loginWay? 'Login':'Signin'" goBack="true">
            <!-- <div slot="changeLogin" class="change_login" @click="changeLoginWay">{{loginWay? "密码登录":"短信登录"}}</div> -->
        </head-top>
        <form class="loginForm" v-if="loginWay">
            <section class="input_container phone_number">
                <input type="text" placeholder="Account" name="phone" maxlength="11" v-model="phoneNumber">
            </section>
        </form>
        <form class="loginForm" v-else>
            <section class="input_container">
                <input type="text" placeholder="Account" v-model.lazy="userAccount">
            </section>
            <section class="input_container">
                <input v-if="!showPassword" type="password" placeholder="Password"  v-model="passWord">
                <input v-else type="text" placeholder="Password"  v-model="passWord">
                <div class="button_switch" :class="{change_to_text: showPassword}">
                    <div class="circle_button" :class="{trans_to_right: showPassword}" @click="changePassWordType"></div>
                    <span>abc</span>
                    <span>...</span>
                </div>
            </section>
        </form>
        
        <div class="login_container" @click="mobileLogin">Login</div>
        <router-link to="/forget" class="to_forget" v-if="!loginWay">Forget password?</router-link>
        <alert-tip v-if="showAlert" :showHide="showAlert" @closeTip="closeTip" :alertText="alertText"></alert-tip>
    </div>
</template>

<script>
    import headTop from '../../components/header/head'
    import alertTip from '../../components/common/alertTip'
    import {localapi, proapi, imgBaseUrl} from 'src/config/env'
    import {mapState, mapMutations} from 'vuex'
    import {mobileCode, checkExsis, sendLogin, getcaptchas, accountLogin} from '../../service/getData'

    export default {
        data(){
            return {
                loginWay: false, 
                showPassword: false, 
                phoneNumber: null, 
                userInfo: null, 
                userAccount: null, 
                passWord: null, 
                codeNumber: null,
                showAlert: false, 
                alertText: null, 
            }
        },
        created(){
            this.getCaptchaCode();
        },
        components: {
            headTop,
            alertTip,
        },
        computed: {
            //判断手机号码
            rightPhoneNumber: function (){
                return /^1\d{10}$/gi.test(this.phoneNumber)
            }
        },
        methods: {
            ...mapMutations([
                'RECORD_USERINFO',
            ]),
            //改变登录方式
            changeLoginWay(){
                this.loginWay = !this.loginWay;
            },
            //是否显示密码
            changePassWordType(){
                this.showPassword = !this.showPassword;
            },
            //获取验证吗，线上环境使用固定的图片，生产环境使用真实的验证码
            async getCaptchaCode(){
                let res = await getcaptchas();
                this.captchaCodeImg = res.code;
            },
            //获取短信验证码
            async getVerifyCode(){
                if (this.rightPhoneNumber) {
                    this.computedTime = 30;
                    this.timer = setInterval(() => {
                        this.computedTime --;
                        if (this.computedTime == 0) {
                            clearInterval(this.timer)
                        }
                    }, 1000)
                    //判读用户是否存在
                    let exsis = await checkExsis(this.phoneNumber, 'mobile');
                    if (exsis.message) {
                        this.showAlert = true;
                        this.alertText = exsis.message;
                        return
                    }else if(!exsis.is_exists) {
                        this.showAlert = true;
                        this.alertText = 'Your phone is not binding';
                        return
                    }
                    //发送短信验证码
                    let res = await mobileCode(this.phoneNumber);
                    if (res.message) {
                        this.showAlert = true;
                        this.alertText = res.message;
                        return
                    }
                    this.validate_token = res.validate_token;
                }
            },
            //发送登录信息
            async mobileLogin(){
                if (this.loginWay) {
                    if (!this.rightPhoneNumber) {
                        this.showAlert = true;
                        this.alertText = 'Wrong phone number';
                        return
                    }
                    this.userInfo = await sendLogin(this.mobileCode, this.phoneNumber, this.validate_token);
                }else{
                    if (!this.userAccount) {
                        this.showAlert = true;
                        this.alertText = 'Account missing!';
                        return
                    }else if(!this.passWord){
                        this.showAlert = true;
                        this.alertText = 'Password missing!';
                        return
                    }
                    this.userInfo = await accountLogin(this.userAccount, this.passWord, this.codeNumber);
                }
                if (!this.userInfo.user_id) {
                    this.showAlert = true;
                    this.alertText = this.userInfo.message;
                    if (!this.loginWay) this.getCaptchaCode();
                }else{
                    this.RECORD_USERINFO(this.userInfo);
                    this.$router.go(-1);

                }
            },
            closeTip(){
                this.showAlert = false;
            }
        }
    }

</script>

<style lang="scss" scoped>
    @import '../../style/mixin';

    .loginContainer{
        padding-top: 1.95rem;
        p, span, input{
            font-family: Helvetica Neue,Tahoma,Arial;
        }
    }
    .change_login{
        position: absolute;
        @include ct;
        right: 0.75rem;
        @include sc(.7rem, #fff);
    }

    .loginForm{
        background-color: #fff;
        margin-top: .6rem;
        .input_container{
            display: flex;
            justify-content: space-between;
            padding: .6rem .8rem;
            border-bottom: 1px solid #f1f1f1;
            input{
                @include sc(.7rem, #666);
            }
            button{
                @include sc(.65rem, #fff);
                font-family: Helvetica Neue,Tahoma,Arial;
                padding: .28rem .4rem;
                border: 1px;
                border-radius: 0.15rem;
            }
            .right_phone_number{
                background-color: #4cd964;
            }
        }
        .phone_number{
            padding: .3rem .8rem;
        }
        .captcha_code_container{
            height: 2.2rem;
            .img_change_img{
                display: flex;
                align-items: center;
                img{
                    @include wh(3.5rem, 1.5rem);
                    margin-right: .2rem;
                }
                .change_img{
                    display: flex;
                    flex-direction: 'column';
                    flex-wrap: wrap;
                    width: 2rem;
                    justify-content: center;
                    p{
                        @include sc(.55rem, #666);
                    }
                    p:nth-of-type(2){
                        color: #3b95e9;
                        margin-top: .2rem;
                    }
                }
            }
        }
    }
    .login_tips{
        @include sc(.5rem, red);
        padding: .4rem .6rem;
        line-height: .5rem;
        a{
            color: #3b95e9;
        }
    }
    .login_container{
        margin: 0 .5rem 1rem;
        @include sc(.7rem, #fff);
        background-color: #4cd964;
        padding: .5rem 0;
        border: 1px;
        border-radius: 0.15rem;
        text-align: center;
    }
    .button_switch{
        background-color: #ccc;
        display: flex;
        justify-content: center;
        @include wh(2rem, .7rem);
        padding: 0 .2rem;
        border: 1px;
        border-radius: 0.5rem;
        position: relative;
        .circle_button{
            transition: all .3s;
            position: absolute;
            top: -0.2rem;
            z-index: 1;
            left: -0.3rem;
            @include wh(1.2rem, 1.2rem);
            box-shadow: 0 0.026667rem 0.053333rem 0 rgba(0,0,0,.1);
            background-color: #f1f1f1;
            border-radius: 50%;
        }
        .trans_to_right{
            transform: translateX(1.3rem);
        }
        span{
            @include sc(.45rem, #fff);
            transform: translateY(.05rem);
            line-height: .6rem;
        }
        span:nth-of-type(2){
            transform: translateY(-.08rem);
        }
    }
    .change_to_text{
        background-color: #4cd964;
    }
    .to_forget{
        float: right;
        @include sc(.6rem, #3b95e9);
        margin-right: .3rem;
    }
</style>
