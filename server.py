from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI(title="StreamPlayer Control Panel Backend")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/dashboard.json")
def get_dashboard():
    return {
        "featured": [
            {
                "id": "m1",
                "title": "Action Movie 2026",
                "poster_url": "https://picsum.photos/300/450?random=1",
                "video_url": "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                "category": "movie",
                "description": "High octane action thriller.",
                "duration": "2h 15m"
            },
            {
                "id": "m2",
                "title": "Sci-Fi Odyssey",
                "poster_url": "https://picsum.photos/300/450?random=2",
                "video_url": "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                "category": "movie",
                "description": "Journey beyond the stars.",
                "duration": "1h 55m"
            }
        ],
        "movies": [
            {
                "id": "m1",
                "title": "Action Movie 2026",
                "poster_url": "https://picsum.photos/300/450?random=1",
                "video_url": "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                "category": "movie",
                "description": "High octane action thriller.",
                "duration": "2h 15m"
            },
            {
                "id": "m2",
                "title": "Sci-Fi Odyssey",
                "poster_url": "https://picsum.photos/300/450?random=2",
                "video_url": "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                "category": "movie",
                "description": "Journey beyond the stars.",
                "duration": "1h 55m"
            }
        ],
        "web_series": [
            {
                "id": "s1",
                "title": "Cyber Chronicles S1",
                "poster_url": "https://picsum.photos/300/450?random=4",
                "video_url": "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                "category": "series",
                "description": "Hacker wars in neon city.",
                "duration": "8 Episodes"
            }
        ],
        "live_matches": [
            {
                "match_id": "ind_vs_aus_2026",
                "title": "India vs Australia - Live T20",
                "is_active": True,
                "streams": [
                    {"source": "Primary Link", "url": "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4", "priority": 1},
                    {"source": "Backup Link 1", "url": "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4", "priority": 2},
                    {"source": "Backup Link 2", "url": "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4", "priority": 3}
                ]
            }
        ]
    }

@app.get("/update.json")
def get_update():
    return {
        "version_code": 1,
        "version_name": "1.0.0",
        "apk_url": "https://example.com/streamplayer.apk",
        "release_notes": "Bug fixes and performance improvements.",
        "is_forced": False
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
