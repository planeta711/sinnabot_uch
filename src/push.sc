theme: /

    state: Cancel
        q!: стоп
        if: !$client.event_arr
            script:
                $reactions.answer("Все уведомления выключены");
        else:
            script:
                $temp.elementForDel = $client.event_arr.pop() 
                $pushgate.cancelEvent($temp.elementForDel);
                $reactions.answer("Все уведомления выключены!");


    state: Reminder
        q!: запустить пуш
        a: Рад что вы согласились на пуш уведомления. В ближайшее время они начнут вам поступать =) 
        if: !$client.event_arr
            script:
                $client.event_arr = [];    
        script: 
            #установка таймзоны Москва для напоминаний-будильников
            $reactions.setClientTimezone("Europe/Moscow");
            #установка английского языка для функции  moment, которая в боте получает название дня недели
            moment.locale('en');
            //счетчик номера напоминания
            $client.reminder_number = 0;
            //время напоминаний
            var now_h = moment($jsapi.timeForZone("Europe/Moscow")).format("HH");
            var now_m= moment($jsapi.timeForZone("Europe/Moscow")).format("mm");
            var now_s= moment($jsapi.timeForZone("Europe/Moscow")).format("ss");
            var now_time = now_h+":"+now_m+":"+now_s;
            
            //формирование полной даты для напоминания
            $client.pushDate = moment($jsapi.dateForZone("Europe/Moscow", "yyyy-MM-dd") + "T" + now_time).add(1, "minute").format();
            
            //первый запуск напоминаний.
            //создание самого напоминания
            $temp.event = $pushgate.createEvent(
                $client.pushDate,
                "reminderEvent",
                {
                    id: $client.reminder_number, ///номер напоминаний
                    text: reminder[$client.reminder_number] ///текст напоминания
                }
            );
            
            $client.event_arr.push($temp.event.id); ///добавление нового напоминания в массив
            
        # a: {{ $client.pushDate }}    
        # a: {{ $temp.event.id }}    
        # a: {{ toPrettyString($client.event_arr)}}
                
            

    # стейт обработки напоминаний
    state: Remind || noContext = true
        event!: reminderEvent
        script:
            #установка таймзоны Москва для напоминаний-будильников
            $reactions.setClientTimezone("Europe/Moscow");
            #установка английского языка для функции  moment, которая в боте получает название дня недели
            moment.locale('en');
            //$client.pushDate = moment($client.pushDate).add(1, "minute").format(); ///для теста напоминания с шагом 5 минут
            $client.pushDate = moment($client.pushDate).add(3, "day").format(); // новая дата для нового напоминания. Тк старое всегда отрабатывает один раз и нужно создавать новые (к старой дате+2день)
            $reactions.answer($request.rawRequest.eventData.text);
            $client.reminder_number = $client.reminder_number + 1;
            
            if (!reminder[$client.reminder_number]) {
            
            } else {
                $temp.event = $pushgate.createEvent(
                    $client.pushDate,
                    "reminderEvent",
                    {
                        id: $request.rawRequest.eventData.id,  
                        text: reminder[$client.reminder_number]
                    }
                );
                
                $client.event_arr.push($temp.event.id); ///добавление нового напоминания в массив
            }
    
    state: eventArr
        q!: покажи клиентский массив
        a: {{ toPrettyString($client.event_arr) }}
    
    
    state: eventArr2
        q!: продвинутый стоп
        a: клиентский массив до удаления {{ toPrettyString($client.event_arr) }}
        script:
            $session.elementForDel = $client.event_arr.pop()
        a: элемент на удаление {{ toPrettyString($session.elementForDel) }}    
        script:    
            $pushgate.cancelEvent($session.elementForDel);
            $reactions.answer("клиентский массив после удаления" + toPrettyString($client.event_arr) );
            $client.event_arr = [];