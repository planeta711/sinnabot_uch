//функция отправки видео
function postVideo(clientid, link, caption) {
    var url = "https://api.telegram.org/bot5154312961:AAGerpMBUOkqahq1pEfM6aE0VhN_vQN9P8I/sendVideo";
    var options = {
        dataType: "json",
        body: {
            "chat_id": clientid,
            "video": link
        }
    };
    var response = $http.post(url, options);
    return response.isOk ? response.data : false;
}

function postDocument(clientid, link, caption) {
    var url = "https://api.telegram.org/bot5154312961:AAGerpMBUOkqahq1pEfM6aE0VhN_vQN9P8I/sendDocument";
    var options = {
        dataType: "json",
        body: {
            "chat_id": clientid,
            "document": link,
            "caption": caption
        }
    };
    var response = $http.post(url, options);
    return response.isOk ? response.data : false;
}

function postData(id) {
    var url = "https://api.telegram.org/bot5154312961:AAGerpMBUOkqahq1pEfM6aE0VhN_vQN9P8I/sendInvoice";
    var options = {
        dataType: "json",
        body: {
            "chat_id":id,
            "title": "Стратегическое управление. Простым языком.",
            "description": "Заполните все поля",
            "payload":{},
            "provider_token":"390540012:LIVE:25855",
            "currency": "RUB",
            "start_parameter":"mybot",
            "photo_url":"https://thumb.tildacdn.com/tild3635-6538-4462-a362-613865643265/-/resize/271x/-/format/webp/photo.jpg",
            "prices":[{"label":"руб","amount": 180000}],
            
            "amount": {
              "value": "1800.00",
              "currency": "RUB"
            },
            "payment_method_data": {
              "type": "bank_card"
            },
            // "confirmation": {
            //   "type": "redirect",
            //   "return_url": "https://www.merchant-website.com/return_url"
            // },
          
            "need_phone_number": true,
            "need_name": true,
            "need_email": true
        }
    };
    var response = $http.post(url, options);
    return response.isOk ? response.data : false;
}

function postData2(id) {
    var url = "https://api.telegram.org/bot5154312961:AAGerpMBUOkqahq1pEfM6aE0VhN_vQN9P8I/answerPreCheckoutQuery";
    var options = {
        dataType: "json",
        body: {
            "pre_checkout_query_id":id,
            "ok": true
        }
    };
    var response = $http.post(url, options);
    return response.isOk ? response.data : false;
}

function postDataVar2(id) {
    var url = "https://api.telegram.org/bot5154312961:AAGerpMBUOkqahq1pEfM6aE0VhN_vQN9P8I/sendInvoice";
    var options = {
        dataType: "json",
        body: {
            "chat_id":id,
            "title": "Стратегическое управление. Простым языком. (PDF)",
            "description": "Заполните все поля",
            "payload":{},
            "provider_token":"390540012:LIVE:25855",
            "currency": "RUB",
            "start_parameter":"mybot",
            "photo_url":"https://thumb.tildacdn.com/tild3635-6538-4462-a362-613865643265/-/resize/271x/-/format/webp/photo.jpg",
            "prices":[{"label":"руб","amount": 34900}],
            "amount": {
              "value": "349.00",
              "currency": "RUB"
            },
            "payment_method_data": {
              "type": "bank_card"
            },
            
            "need_phone_number": true,
            "need_name": true,
            "need_email": true
        }
    };
    var response = $http.post(url, options);
    return response.isOk ? response.data : false;
}

function postData2Var2(id) {
    var url = "https://api.telegram.org/bot5154312961:AAGerpMBUOkqahq1pEfM6aE0VhN_vQN9P8I/answerPreCheckoutQuery";
    var options = {
        dataType: "json",
        body: {
            "pre_checkout_query_id":id,
            "ok": true
        }
    };
    var response = $http.post(url, options);
    return response.isOk ? response.data : false;
}

function GetUrl(utm_url) {
    var url = "https://clck.ru/--?url=" + utm_url;
    var options = {
        dataType: "json"
    };
    var response = $http.get(url, options);
    return response.isOk ? response.data : false;
}