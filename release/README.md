## mock-server


```bytes
房间1的数据
3E 7B 3F 01 00 00 00 00 00 00 00 00 00 00 00 00 00 03 00 01 00 7D 0D 0A 

房间1 2的数据
3E 7B 3F 01 00 00 00 00 00 00 00 00 00 00 00 00 00 03 00 01  00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 02 00 7D 0D 0A 
```



## JSON配置数据说明
``` json
{
  "serial": {
    "name": "COM2",
    "baud": 9600
  },
  "games": [
    {
      "id": 1,
      "title": "游戏A",
      "enable": true,
      "note": "这是一个小游戏A",
      "rooms": [
        {
          "id": 1,
          "title": "房间A",
          "online": false,
          "levels": [
            {
              "title": "准备",
              "value": 1
            },
            {
              "title": "关卡1",
              "value": 2
            },
            {
              "title": "关卡2",
              "value": 3
            }
          ],
          "current": -1
        },
        {
          "id": 2,
          "title": "房间B",
          "online": false,
          "levels": [
            {
              "title": "准备",
              "value": 1
            },
            {
              "title": "关卡1",
              "value": 2
            },
            {
              "title": "关卡2",
              "value": 3
            }
          ],
          "current": -1
        }
      ],
      "level": {
        "reset": 1,
        "ready": 0,
        "start": 2
      }
    },
    {
      "id": 2,
      "title": "游戏B",
      "enable": false
    },
    {
      "id": 3,
      "title": "游戏C",
      "enable": false
    },
    {
      "id": 4,
      "title": "游戏D",
      "enable": false
    }
  ],
  "status": 0,
  "queue": {
    "messages": 10,
    "request": 10
  }
}
```


