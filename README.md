# OVH mail redirection manager (for Android)

This Android application allows you to manage your OVH redirections, directly on your phone, using your OVH API keys.
You can basically add redirections for your domain, et remove ones. You can choose whether you want to store the keys to your phone or not, depending on the level of privacy you want, and how much you care about the Android botnet.

## Getting Started

This is an Android Studio project, so the easiest way to get it compiled is to open the project with Android Studio, let it install required tools with Gradle, and build the app.

Once you have it on your phone, you will have to enter your API keys and domain, then you're done.
Available OVH endpoints are for now : 
        'ovh-eu'        => 'https://eu.api.ovh.com/1.0',
        'ovh-ca'        => 'https://ca.api.ovh.com/1.0',
        'kimsufi-eu'    => 'https://eu.api.kimsufi.com/1.0',
        'kimsufi-ca'    => 'https://ca.api.kimsufi.com/1.0',
        'soyoustart-eu' => 'https://eu.api.soyoustart.com/1.0',
        'soyoustart-ca' => 'https://ca.api.soyoustart.com/1.0',
        'runabove-ca'   => 'https://api.runabove.com/1.0'
So if you use OVH europe, type 'ovh-eu'.

### Issues

Well there is one issue, you might encounter: If you add an redirection, and then quickly remove it after, it may fails to remove it.
This means you have been to fast for the OVH server to handle your request, congratulations, I did not succeed to reproduce the issue twice, and god knows I'm fast as hell.
However there is a workaround for this: be patient.

### Prerequisites

If you haven't done so, you will need to generate your OVH API keys. For this purpose, the better way to do it is to follow [the tutorial on OVH website](https://api.ovh.com/g934.first_step_with_api).

But since I'm a nice guy, I'll give you the curl command to generate a consumer key with the needed access as I know otherwise you'll spend three hours figuring out how the hell it works:

```
curl -XPOST -H"X-Ovh-Application: yourapplicationkey" -H "Content-type: application/json" \
https://eu.api.ovh.com/1.0/auth/credential  -d '{
    "accessRules": [
        {
            "method": "GET",
            "path": "/email/domain/youdomain.org/redirection"
        },
        {
            "method": "GET",
            "path": "/email/domain/yourdomain.org/redirection/*"
        },
        {
            "method": "POST",
            "path": "/email/domain/yourdomain.org/redirection"
        },
        {
            "method": "DELETE",
            "path": "/email/domain/yourdomain.org/redirection/*"
        },
        {
            "method": "POST",
            "path": "/email/domain/yourdomain.org/redirection/*"
        }
    ],
    "redirection":"whateverwebpageyouwant.org"
}'
```

Well don't forget to replace `youdomain.org` with your actual domain, `whateverwebpageyouwant.org` with whatever web page you want, and `yourapplicationkey` with your application key. Straight, right ?
The rest of the job is up to you.

Oh! forgot to tell you have to have an OVH domain and mailbox. 

### Installing

Well I assume you know how to install an Android application. (click the green play button after connecting your device to your computer)
Building the APK and transfering it to your phone also work.

## License

This project is licensed under the GPLv3 License - see the [LICENSE.md](LICENSE.md) file for details

## Contributing

If you want to contribute, don't be shy, get your pull request open. I may merge it. Or not. Then we could go for a beer together, singing the glory of our great app.

