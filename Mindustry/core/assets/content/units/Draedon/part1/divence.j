{
	"type":"mech",
	"name":"分歧",
	"description":"向敌人发射闪电",
	"details":"低级嘉登造物，它们仅在一些小型而无关紧要的工厂或实验室担任防御任务。",

	"health":1540,
	"speed":0.6,
	"rotateSpeed":2.5,
	"hitSize":15,
	"hitSize":15,
	"armor":9,
	"itemCapacity":40,
	"boostMultiplier":2.0,
	"canBoost":true,
	"mechLandShake":2,
	"riseSpeed":0.05,
	"mechFrontSway":0.55,
	"engineOffset":7.4,
	"engineSize":4.25,

	"research":{
	"parent":"primary-machine-manufacturer",
	"requirements":[
	"chromium/5000"
	"silicon/4000",
	]
	"objectives":[
	"tank-manufacturer",
	]	},

	"immunities":[
	"wulfrum-amplify"
	],

	"weapons":[
		{"name":"divergence-lighting",
		"mirror":true,
		"top":false,
		"rotate":true,
		"rotationLimit":15,
		"x":8,"y":1,
		"shootY":9.5,
		"reload":90,
		"shootCone":25,
		"shootStatus":"slow",
		"shootStatusDuration":90,
		"continuous":true;
		"shootSound":"beam",

		"bullet":{
		"type":"ContinuousLaserBulletType"
		"length":120,"width":2.55,

		"incendChance":0.025,
		"incendSpread":5.0,
		"incendAmount":1,
		"shake":3,

		"oscScl":0.4,
		"oscMag":1.5,
		"lifetime":90,
		"lightColor":"DEEDFFFF",
		"hitColor":"DEEDFFFF",
		"hitEffect":"DEEDFFFF",
		"shootEffect":"DEEDFFFF",
		"smokeEffect":"shootBigSmoke"
		}	}	]
}