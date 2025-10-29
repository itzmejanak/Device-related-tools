package com.brezze.share.utils.common.enums.hint;

public enum Hint implements HintEnum {

    /**
     * APP统一返回前端消息枚举类
     */
    SUCCESS("0","成功","Success","Succes","Succès","Successo","Erfolg","Успех","Sucesso","Éxito","Uspeh","Επιτυχία","Uspjeh","สำเร็จ","ສຳເລັດ ","Успіх"),
    FAILURE("1","失败","Failure","Mislukking","Échec","Fallimento","Versagen","Отказ","Falha","Falla","Neuspeh","Αποτυχία","Neuspjeh","ล้มเหลว","ລົ້ມເຫຼວ","Відмова"),
    SYSTEM_MAINTENANCE("10100","系统维护中","System maintenance in progress","Systeemonderhoud in vooruitgang","Maintenance du système en cours","Manutenzione del sistema in corso","Systemwartung läuft","Выполняется техническое обслуживание системы","Manutenção do sistema em andamento","Mantenimiento del sistema en curso.","Vzdrževanje sistema v teku","Συντήρηση συστήματος σε εξέλιξη","Održavanje sustava u tijeku","ระบบกำลังอยู่ในระหว่างการบำรุงรักษา","ລະບົບຢູ່ໃນຂັ້ນຕອນອັປເດດໂປຮມແກຮມ","Триває технічне обслуговування системи"),
    SYSTEM_BUSY("10101","系统正忙。请稍后再试","System is busy. Please try again later","Het systeem is bezet. Probeer het later opnieuw","Le système est occupé. Veuillez réessayer plus tard","Il sistema è occupato. Per favore riprova più tardi","Das System ist beschäftigt. Bitte versuchen Sie es später noch einmal","Система занята. Пожалуйста, повторите попытку позже","O sistema está ocupado. Por favor, tente novamente mais tarde","El sistema está ocupado. Por favor, inténtelo de nuevo más tarde","Sistem je zaseden. Prosim poskusite kasneje","Το σύστημα είναι απασχολημένο. Παρακαλώ δοκιμάστε ξανά αργότερα","Sustav je zauzet. Pokušajte ponovo kasnije","ระบบกำลังใช้งานอยู่ กรุณาลองใหม่ภายหลัง","ລະບົບກຳລັງໃຊ້ງານຢູ່ ກະລຸນນາລໍຖ້າ","Система перенавантажена. Будь ласка, спробуйте пізніше"),
    BAD_REQUEST("10400","客户端请求的语法错误，服务器无法理解","Bad Request","Slecht verzoek","Mauvaise demande","Brutta richiesta","Ungültige Anforderung","Неверный запрос","Pedido ruim","Solicitud incorrecta","Slaba prošnja","Λάωθασμένο αίτημα","Neispravan zahtjev","คำขอที่ไม่ถูกต้อง","ຄຳຮ້ອງຂໍທີ່ບໍ່ດີ","Помилковий запит"),
    UNAUTHORIZED("10401","请求要求用户的身份认证","Request user authentication","Gebruikersverificatie aanvragen","Demander l'authentification de l'utilisateur","Richiedi l'autenticazione dell'utente","Benutzerauthentifizierung anfordern","Запросить аутентификацию пользователя","Solicitar autenticação do usuário","Solicitar autenticación de usuario","Zahtevajte preverjanje pristnosti uporabnika","Αίτημα επαλήθευσης χρήστη","Zahtjev za provjeru autentičnosti korisnika","การยืนยันตัวตนของผู้ใช้","ຮ້ອງຂໍການກວດສອບຜູ້ໃຊ້","Запит автентифікації користувача"),
    FORBIDDEN("10403","服务器理解请求客户端的请求，但是拒绝执行此请求","Access to this resource on the server is denied","Toegang tot deze bron op de server wordt geweigerd","L'accès à cette ressource sur le serveur est refusé","L'accesso a questa risorsa sul server è negato","Der Zugriff auf diese Ressource auf dem Server wird verweigert","Доступ к этому ресурсу на сервере запрещен","O acesso a este recurso no servidor foi negado","El acceso a este recurso en el servidor está denegado.","Dostop do tega vira na strežniku je zavrnjen","Δεν επιτρέπεται η πρόσβαση στον διακομιστή","Pristup ovom resursu na poslužitelju je odbijen","ปฏิเสธการเข้าถึงทรัพยากรนี้บนเซิร์ฟเวอร์","ການເຂົ້າເຖິງຊັບພະຍາກອນນີ້ຢູ່ໃນເຊີບເວີຖືກປະຕິເສດ","Доступ до цього ресурсу на сервері обмежено"),
    NOT_FOUND("10404","服务器资源未找到","Server resource not found","Serverbron niet gevonden","Ressource serveur introuvable","Risorsa del server non trovata","Serverressource nicht gefunden","Ресурс сервера не найден","Recurso do servidor não encontrado","Recurso del servidor no encontrado","Strežniški vir ni najden","Δεν βρέθηκε ο διακομιστής","Resurs poslužitelja nije pronađen","ไม่พบทรัพยากรเซิร์ฟเวอร์","ບໍ່ພົບຊັບພະຍາກອນຂອງເຊີບເວີ","Ресурс на сервері не знайдено"),
    METHOD_NOT_ALLOWED("10405","客户端请求中的方法被禁止","Method in client request is forbidden","Methode in klantverzoek is verboden","La méthode dans la demande du client est interdite","Il metodo nella richiesta del client è vietato","Methode in Client-Anfrage ist verboten","Метод в клиентском запросе запрещен","O método na solicitação do cliente é proibido","El método en la solicitud del cliente está prohibido.","Metoda v zahtevi stranke je prepovedana","Η μέθοδος στην αίτηση πελάτη απαγορεύεται","Način u zahtjevu klijenta je zabranjen","วิธีการในการร้องขอของลูกค้าเป็นสิ่งต้องห้าม","ວິທີການໃນຄໍາຮ້ອງຂໍຂອງລູກຄ້າແມ່ນຖືກຫ້າມ","Метод у клієнтському запиті заборонено"),
    BAD_PARAMETER("10406","请求的参数有误","Wrong parameters for the request","Verkeerde parameters voor het verzoek","Mauvais paramètres pour la requête","Parametri errati per la richiesta","Falsche Parameter für die Anfrage","Неверные параметры запроса","Parâmetros incorretos para a solicitação","Parámetros incorrectos para la solicitud","Napačni parametri za zahtevo","Λανθασμένες παράμετροι για το αίτημα","Pogrešni parametri zahtjeva","พารามิเตอร์ที่ร้องขอผิดพลาด","ຕົວກໍານົດການຜິດພາດສໍາລັບການຮ້ອງຂໍ","Помилкові параметри запиту"),
    INTERNAL_SERVER_ERROR("10500","服务器内部错误","Internal server error","Interne serverfout","Erreur interne du serveur","Errore interno del server","Interner Serverfehler","Внутренняя ошибка сервера","Erro do Servidor Interno","Error Interno del Servidor","Napaka notranjega strežnika","Εσωτερικό σφάλμα διακομιστή","Interna pogreška poslužitelja","ข้อผิดพลาดของเซิร์ฟเวอร์ภายใน","ເຊີບເວີພາຍໃນຜິດພາດ","Внутрішня помилка сервера"),


    COMMUNICATION_DEVICE_NOT_EXISTS("24005","设备不存在","Device does not exist","Устройство не существует","L'appareil n'existe pas","Il dispositivo non esiste","Gerät existiert nicht","Устройство не существует","O dispositivo não existe","El dispositivo no existe","","","","","ບໍ່ມີອຸປະກອນ","Пристрій не існує"),
    API_DEVICE_HAS_BIND("24006","机柜已绑定","Station is assigned","Станция закреплена","La station est attribuée","La stazione è assegnata","Station ist zugewiesen","Станция назначена","A estação está atribuída","La estación está asignada","","","","","ໄດ້ມິການຜູກມັດກັບຕູ້ ","Станцію призначено"),
    API_DEVICE_BIND_REPEAT("24007","机柜已绑定，请勿重复操作","Station is assigned,please do not repeat operation","Станция закреплена, пожалуйста, не повторяйте операцию","La station est attribuée, veuillez ne pas répéter l'opération","La stazione è assegnata, non ripetere l'operazione","Station ist zugewiesen, bitte wiederholen Sie den Vorgang nicht","Станция назначена, пожалуйста, не повторяйте операцию","A estação está atribuída, não repita a operação","La estación está asignada, no repita la operación","","","","","ໄດ້ມີການຜູກມັດກັບຕູ້, ບໍ່ຈຳເປັນຕ້ອງຕື່ມຂໍ້ມູນອີກຄັ້ງ","Станцію призначено, будь ласка, не повторюйте операцію"),
    COMMUNICATION_DEVICE_ERROR_IMEI_FORMAT("24008","IMEI格式不正确","Incorrect IMEI","Неверный формат IMEI","IMEI incorrect","IMEI errato","Falsche IMEI","Неверный IMEI","IMEI incorreto","IMEI incorrecto","","","","","ຮູບແບບ IMEI ບໍ່ຖືກຕ້ອງ","Неправильний IMEI"),

    ;

    private String code;

    private String cnDescription;

    private String enDescription;

    private String nlDescription;

    private String frDescription;

    private String itDescription;

    private String deDescription;

    private String ruDescription;

    private String ptDescription;

    private String esDescription;

    private String slDescription;

    private String elDescription;

    private String hrDescription;

    private String thDescription;

    private String loDescription;

    private String ukDescription;

    private Hint() {
    }

    Hint(String code, String cnDescription, String enDescription, String nlDescription, String frDescription, String itDescription, String deDescription, String ruDescription, String ptDescription, String esDescription, String slDescription, String elDescription, String hrDescription, String thDescription, String loDescription, String ukDescription) {
        this.code = code;
        this.cnDescription = cnDescription;
        this.enDescription = enDescription;
        this.nlDescription = nlDescription;
        this.frDescription = frDescription;
        this.itDescription = itDescription;
        this.deDescription = deDescription;
        this.ruDescription = ruDescription;
        this.ptDescription = ptDescription;
        this.esDescription = esDescription;
        this.slDescription = slDescription;
        this.elDescription = elDescription;
        this.hrDescription = hrDescription;
        this.thDescription = thDescription;
        this.loDescription = loDescription;
        this.ukDescription = ukDescription;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getZhDescription() {
        if (this.cnDescription != null && this.cnDescription.trim().length() > 0) {
            return String.format("S-%s(%s)", this.cnDescription, this.code);
        }
        return "";
    }

    @Override
    public String getEnDescription() {
        if (this.enDescription != null && this.enDescription.trim().length() > 0) {
            return String.format("S-%s(%s)", this.enDescription, this.code);
        }
        return "";
    }

    @Override
    public String getNlDescription() {
        if (this.nlDescription != null && this.nlDescription.trim().length() > 0) {
            return String.format("S-%s(%s)", this.nlDescription, this.code);
        }
        return "";
    }

    @Override
    public String getFrDescription() {
        if (this.enDescription != null && this.frDescription.trim().length() > 0) {
            return String.format("S-%s(%s)", this.frDescription, this.code);
        }
        return "";
    }

    @Override
    public String getItDescription() {
        if (this.enDescription != null && this.itDescription.trim().length() > 0) {
            return String.format("S-%s(%s)", this.itDescription, this.code);
        }
        return "";
    }

    @Override
    public String getDeDescription() {
        if (this.enDescription != null && this.deDescription.trim().length() > 0) {
            return String.format("S-%s(%s)", this.deDescription, this.code);
        }
        return "";
    }

    @Override
    public String getRuDescription() {
        if (this.enDescription != null && this.ruDescription.trim().length() > 0) {
            return String.format("S-%s(%s)", this.ruDescription, this.code);
        }
        return "";
    }

    @Override
    public String getPtDescription() {
        if (this.enDescription != null && this.ptDescription.trim().length() > 0) {
            return String.format("S-%s(%s)", this.ptDescription, this.code);
        }
        return "";
    }

    @Override
    public String getEsDescription() {
        if (this.enDescription != null && this.esDescription.trim().length() > 0) {
            return String.format("S-%s(%s)", this.esDescription, this.code);
        }
        return "";
    }

    @Override
    public String getSlDescription() {
        if (this.slDescription != null && this.slDescription.trim().length() > 0) {
            return String.format("S-%s(%s)", this.slDescription, this.code);
        }
        return "";
    }

    @Override
    public String getElDescription() {
        if (this.elDescription != null && this.elDescription.trim().length() > 0) {
            return String.format("S-%s(%s)", this.elDescription, this.code);
        }
        return "";
    }

    @Override
    public String getHrDescription() {
        if (this.hrDescription != null && this.hrDescription.trim().length() > 0) {
            return String.format("S-%s(%s)", this.hrDescription, this.code);
        }
        return "";
    }

    @Override
    public String getThDescription() {
        if (this.thDescription != null && this.thDescription.trim().length() > 0) {
            return String.format("S-%s(%s)", this.thDescription, this.code);
        }
        return "";
    }

    @Override
    public String getLoDescription() {
        if (this.loDescription != null && this.loDescription.trim().length() > 0) {
            return String.format("S-%s(%s)", this.loDescription, this.code);
        }
        return "";
    }

    @Override
    public String getUkDescription() {
        if (this.ukDescription != null && this.ukDescription.trim().length() > 0) {
            return String.format("S-%s(%s)", this.ukDescription, this.code);
        }
        return "";
    }
}
