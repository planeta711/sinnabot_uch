require: slotfilling/slotFilling.sc
  module = sys.zb-common
require: dateTime/moment.min.js
    module = sys.zb-common 
require: dictionaries/answers.yaml
    var = answers
    name = answers
require: dictionaries/links.yaml
    var = links
    name = links
#словарь для хранения текста напоминаний   
require: dictionaries/reminder.yaml
    var = reminder
    name =  reminder 

require: testzone.sc
require: push.sc
require: functions.js

init:
    
    $global.$ = {
        __noSuchProperty__: function(property) {
            return $jsapi.context()[property];
        }
    };
    
    bind("postProcess", function($context) {
        $context.session.lastState = $context.currentState;
    });
    
    # bind("postProcess", function($context) {
    #     //$reactions.answer("контекст");
    #     $reactions.answer(toPrettyString($context));
    # });
    
    
    
theme: /

########### коллбек дата ###########    
    state: ReceiptCallbackQuery
        event: telegramCallbackQuery
        if: $request.rawRequest.callback_query.data === "Production" 
            script: $analytics.setSessionData("Тип бизнеса", "Производство")
            go!: /Problems 
        if: $request.rawRequest.callback_query.data === "Services" 
            script: $analytics.setSessionData("Тип бизнеса", "Услуги")
            go!: /Problems 
        if: $request.rawRequest.callback_query.data === "Sales" 
            script: $analytics.setSessionData("Тип бизнеса", "Продажи")
            go!: /Problems 
        if: $request.rawRequest.callback_query.data === "ProblemMoney" 
            script: $analytics.setSessionData("Проблемы", "Деньги")
                $session.reason = "Денег"
            go!: /VideoOne
        if: $request.rawRequest.callback_query.data === "ProblemStaff" 
            script: $analytics.setSessionData("Проблемы", "Персонал")
                $session.reason = "Персонала"
            go!: /VideoOne
        if: $request.rawRequest.callback_query.data === "ProblemTime" 
            script: $analytics.setSessionData("Проблемы", "Время")
                $session.reason = "Времени"
            go!: /VideoOne
        if: $request.rawRequest.callback_query.data === "videoTwo" 
            go!: /VideoTwo  
        if: $request.rawRequest.callback_query.data === "videoThree" 
            go!: /VideoThree 
        if: $request.rawRequest.callback_query.data === "videoFour" 
            go!: /VideoFour
        if: $request.rawRequest.callback_query.data === "PushYes" 
            go!: /Reminder
        
 
####################################

    state: Start
        q!: * ($regex</start>/*start) *
        script: 
            $session.counter = 0;
            if ($request && $request.rawRequest && $request.rawRequest.message && $request.rawRequest.message.from && $request.rawRequest.message.from.username) {
                $client.username = $request.rawRequest.message.from.username;
                $analytics.setSessionData("Имя в телеграмме", "@" + $client.username)
            }
            # запоминаем user id для utm метки
            if ($request && $request.rawRequest && $request.rawRequest.message && $request.rawRequest.message.from && $request.rawRequest.message.from.id) {
                $client.user_id = $request.rawRequest.message.from.id
                $analytics.setSessionData("ID клиента", $client.user_id)
            }
            # if (!$session.timeoutTime) {
            #     $session.timeoutTime = 1800
            # }
        go!: /BeforBegining

########### первый чеклист ###########    
    state: BeforBegining
        script: postDocument($client.user_id, links["FirstChecklist"], "Мы подготовили тебе еще один чек-лист, который поможет тебе сделать системным твой бизнес за 7 шагов. Его можно получить, пройдя небольшой видео-квест. Всего будет 5 видео, каждое не более 3-4 минут. Поехали!")
        script: $reactions.timeout({interval: 10, targetState: "/Begining"}); 
        
    state: Begining    
        a: Как управлять бизнесом в новых реалиях?  Как выйти из операционки и поставить исполнительного директора?  Как настроить правильную финансовую систему в своей компании? Обо всем этом расскажу ниже. Но, вначале, давай познакомимся.
        script: $reactions.timeout({interval: 1, targetState: "/About"});

########### приветственное видео ###########     
    state: About
        script:
            postVideo($client.user_id, links["Intro"])
            $reactions.timeout({interval: 1, targetState: "/AboutText"});
    
    state: AboutText
        a: Какой у тебя бизнес?
        inlineButtons:
            {text: "Производство", callback_data: "Production"}
            {text: "Услуги", callback_data: "Services"}  
            {text: "Продажи", callback_data: "Sales"}

    state: Problems
        a: Какой вопрос бизнеса сейчас самый актуальный?   
        script: $session.owner = true;
        inlineButtons:
            {text: "Деньги", callback_data: "ProblemMoney"}
            {text: "Персонал", callback_data: "ProblemStaff"}  
            {text: "Время", callback_data: "ProblemTime"}
        a: если у тебя другой актуальный вопрос - напиши о нем сюда
        
        state: Other
            q: * 
            script: $analytics.setSessionData("Проблемы", $parseTree.text)
                $session.reason = $parseTree.text
            go!: /VideoOne

########### цикл видео ###########  
    state: VideoOne
        script:
            postVideo($client.user_id, links["VideoOne"])
            $reactions.timeout({interval: 1, targetState: "/VideoOneText"});
    
    state: VideoOneText
        a: Сейчас расскажу как создать системный бизнес и выйти из операционки.
        inlineButtons:    
            {text: "Смотреть дальше", callback_data: "videoTwo"}
    
    state: VideoTwo
        script:
            postVideo($client.user_id, links["VideoTwo"])
            $reactions.timeout({interval: 1, targetState: "/VideoTwoText"});
        
    state: VideoTwoText    
        a: Этапы модели "Свобода в бизнесе" я раскрываю здесь.
        inlineButtons:    
            {text: "Смотреть дальше", callback_data: "videoThree"}

    state: VideoThree
        script:
            postVideo($client.user_id, links["VideoThree"])
            $reactions.timeout({interval: 1, targetState: "/VideoThreeText"});
            
    state: VideoThreeText   
        a: Продолжение предыдущего видео.
        inlineButtons:    
            {text: "Смотреть дальше", callback_data: "videoFour"}
    
    state: VideoFour
        q!: тест два
        script:
            postVideo($client.user_id, links["VideoFour"])
            $reactions.timeout({interval: 1, targetState: "/VideoFourText"});
        
    state: VideoFourText    
        q!: тест три
        a: Что нужно сделать, чтобы внедрить.
        script: $reactions.timeout({interval: 240, targetState: "/Checklist"});

########### документы и тексовки ###########    
    
    state: Checklist   
        script: postDocument($client.user_id, links["Document"], "Обещанный чек-лист")
        script: $reactions.timeout({interval: 1, targetState: "/ChecklistPDF"}); 
        

    state: ChecklistPDF
        q!: тест четыре
        a: Этот лист основан на многолетней практике внедрения модели "Свобода в бизнесе". Но, конечно же, невозможно в рамках чек-листа расписать все инструкции как реализовать эти шаги на деле. Но хорошая новость в том, что мы можем рассказать тебе как это сделать. Напиши сюда или позвони по телефону, указанному в конце чек-листа и мы с радостью поможем тебе
        script: $reactions.timeout({interval: 10, targetState: "/AboutBook"}); 
    
    state: AboutBook    
        a: 🔥 Сейчас всего за 1800 рублей ты можешь получить книгу «Стратегическое управление простым языком» 👍 \n\nиз которой ты узнаешь \n\n▶️Как управлять будущим \n▶️Как решать проблемы, чтобы они не возникали повторно \n▶️Как ставить исполнительного директора \n▶️Как наладить оперативное управление \n▶️Как оперативное управление соотносится со стратегическим \n▶️Как вовлекать в достижение своих целей персонал \n▶️Какова основная обязанность собственника \n▶️Почему некоторые компании становятся крупными \n▶️Чем отличается стратегическое планирование от стратегического управления \n▶️Как правильно ставить цели для компании \n▶️Как сформировать настоящую команду \n▶️Как перейти на стратегическое управление \n\n ➕Полезные файлы и чек-листы в этом боте еженедельно на интересную для тебя тему \n ➕Возможность пройти со скидкой в 25% онлайн-курс по этой книге или \n ➕Возможность пройти со скидкой в 25% оффлайн мастер-класс в Москве "Стратегическое управление как путь к свободе в бизнесе
        a: Вы можете купить книгу, написав боту "купить книгу" и оплатив ее здесь. 
        script:
            $client.prosTime = moment().format("YYYY-MM-DDTHH:mm:ss");
            $reactions.timeout({interval: 3600, targetState: "/FullPros"});

    state: FullPros
        q!: тест опоздал
        a: Увы, время для специальных условий вышло. Но книгу ты по-прежнему можешь купить на сайт https://alliance-strateg.ru/ и начать свой путь к свободе.
        a: Жаль, если ты не успел приобрести книгу и получить все бонусы. Но есть хорошая новость: ты можешь прямо сейчас приобрести PDF-версию книги всего за 349 рублей и, даже если ты ИП, который работает сам на себя, всё равно начать делать шаги к систематизации своего бизнеса! Напиши в бот "купить книгу за 349 рублей" и начинай делать шаги!
        go!: /PushText
    
    state: noMatch 
        event!: noMatch 
        a: Для связи с нами вы можете зайти на сайт https://alliance-strateg.ru/ или позвонить по телефону +7 (925) 000-33-88

    state: Payment
        q!: * (payment test/купить кни*/хочу кни*) *
        # отправляем метод sendInvoice
        script: $session.conTime = moment().format("YYYY-MM-DDTHH:mm:ss");
        script: $session.dif = moment($session.conTime).diff(moment($client.prosTime), "minutes")
        # a: {{ $session.dif }}
        if: $session.dif > 60
            go!: /FullPros
        script:
            var id = $request.rawRequest.message.chat.id;
            # $reactions.answer(toPrettyString(id))
            # $reactions.answer(toPrettyString($request.rawRequest))
            $temp.response = postData(id);

        state: Precheckout
            event: telegramPrecheckoutEvent
            # отправляем метод answerPreCheckoutQuery
            script:
                var id = $request.query;
                $temp.response = postData2(id);


            state: PaymentFailed
                event: telegramPaymentFailedEvent
                a: Возникли неполадки. Пожалуйста, напишите нам на сайте https://alliance-strateg.ru/ или позвонить по телефону +7 (925) 000-33-88

            state: PaymentSuccessful
                event: telegramPaymentSuccessEvent
                # при успешной оплате формируем ссылку на контент с utm меткой и делаем ссылку короткой
                script: 
                    $temp.url = "https://intensive-online.ru/perenos01?utm_source=bot&utm_medium=" + $client.user_id
                    var utm_url = $temp.url
                    $temp.response = GetUrl(utm_url)
                a: Спасибо за Ваш заказ! В ближайшее время с Вами свяжется наш менеджер и обсудит наиболее комфортный способ получения Вами книги. А пока Вы можете изучать здесь полезную рассылку в отношении денег, времени и персонала. 
                go!: /PushText

            state: CatchAll
                event: noMatch
                a: Платеж не прошел. Проверьте, пожалуйста, правильность введенных данных и повторите попытку.
        
    
    state: PaymentVarTwo
        q!: * (payment test two/купить кни* за/хочу кни* за) *
        # отправляем метод sendInvoice
        script:
            var id = $request.rawRequest.message.chat.id;
            $temp.response = postDataVar2(id);

        state: Precheckout
            event: telegramPrecheckoutEvent
            # отправляем метод answerPreCheckoutQuery
            script:
                var id = $request.query;
                $temp.response = postData2Var2(id);


            state: PaymentFailed
                event: telegramPaymentFailedEvent
                a: Возникли неполадки. Пожалуйста, напишите нам на сайте https://alliance-strateg.ru/ или позвонить по телефону +7 (925) 000-33-88

            state: PaymentSuccessful
                event: telegramPaymentSuccessEvent
                # при успешной оплате формируем ссылку на контент с utm меткой и делаем ссылку короткой
                script: 
                    $temp.url = "https://intensive-online.ru/perenos01?utm_source=bot&utm_medium=" + $client.user_id
                    var utm_url = $temp.url
                    $temp.response = GetUrl(utm_url)
                a: Спасибо за Ваш заказ! 
                script: postDocument($client.user_id, links["Book"], "Высылаю книгу по Стратегическому Управлению")
                go!: /PushText

            state: CatchAll
                event: noMatch
                a: Платеж не прошел. Проверьте, пожалуйста, правильность введенных данных и повторите попытку.
        
        
    state: PushText
        q!: тест пуш
        a: 📬 Тебе полезное письмо! \n\n Не важно - купил ты книгу или не купил, но я понял, что тебя волнует вопрос {{$session.reason}}. И, поэтому, каждую неделю готов присылать тебе тексты, таблицы, инструкции, чек-листы на твою тематику - варианта "бери и делай". Не понравится - в любой момент можешь написать "стоп" в бот и рассылка прекратится. А если понравится, ты сможешь посмотреть и другие темы 📰🗞. Если готов, то нажимай на кнопку "да". 
        inlineButtons:    
            {text: "Да", callback_data: "PushYes"}
    
    
    #1. дописать разные пуш уведомления. 2. поменять таймауты . 3. получить от татьяны пуш уведомления. 4. получить книгу в пдф. 5. дописать 