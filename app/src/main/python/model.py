from gradio_client import Client

def model(keyword, video):
    video_dict = {
        "video": video,
        "subtitles": None,
    }
    client = Client("mochaminte/bsldict")
    result = client.predict(
            keyword,	# str  in 'keyword' Textbox component
            video_dict,	# Dict(video: filepath, subtitles: filepath | None)  in 'input_video' Video component
            api_name="/predict"
    )
    return float(result) # convert String to float
