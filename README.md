<div align="center">
<img src='https://github.com/karya-inc/RawAudioRecorder/assets/69595691/1d70ff80-7639-4ab7-8fd4-3da69d95ca4e' width='256px' />
</div>

<h1 align="center">RawAudioRecorder</h1>

</br>

<p align="center">
  <img alt="API" src="https://img.shields.io/badge/Api%2021+-50f270?logo=android&logoColor=black&style=for-the-badge"/></a>
  <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-a503fc?logo=kotlin&logoColor=white&style=for-the-badge"/></a>
<p/>

<p align="center">A lightweight audio recording library that records in .wav format</p>

# Gradle

Kotlin: build.gradle.kts
```kotlin
dependencies {
  implementation("com.github.karya-inc:rawaudiorecorder:<latest_release>")
}
```

Groovy: build.gradle
```kotlin
dependencies {
  implementation 'com.github.karya-inc:rawaudiorecorder:<latest_release>'
}
```

# Usage

Create an instance of RecorderEventListener
```kotlin
val listener = object : RecorderEventListener {
        override fun onAmplitudeChange(amplitude: Int) {
           
        }

        override fun onRecorderStateChanged(state: RecorderState) {
            when (state) {
                RecorderState.PREPARED -> { }
                RecorderState.RECORDING -> { }
                RecorderState.PAUSED -> { }
                RecorderState.STOPPED -> { }
            }
        }

        override fun onProgress(timeMS: Long) {
            
        }

    }
```

Create an instance of RawAudioRecorder. 
RawAudioRecoder needs a CoroutineScope, which can be lifecyclsScope if you're creating it inside Activity or preferably viewModelScope when using in an MVVM project. 
```kotlin
val recorder = RawAudioRecorder(listener, viewModelScope)
```

Prepare the recorder
```kotlin
 recorder.prepare(filePath = audioFilepath, config = RecorderConfig(), suppressNoise = true)
```
Start the recording
```kotlin
recorder.startRecording()
```

### Available interactions
```kotlin
// Pause
recorder.pauseRecording()
// Resume
recorder.resumeRecording()
// Stop
recorder.stopRecording()
```

### Available Configurations
```kotlin
data class RecorderConfig(
    var sampleRate: SampleRate = SampleRate._16_K,
    var channels: Int = AudioFormat.CHANNEL_IN_MONO,
    var audioEncoding: AudioEncoding = AudioEncoding.PCM_16BIT
)
```

| Configuration | Available values                                                                 |
|---------------|----------------------------------------------------------------------------------|
| sampleRate    | SampleRate._8_K (8Khz)                                                           |
|               | SampleRate._16_K (16Khz)                                                         |
|               | SampleRate._44_K (44.1Khz)                                                       |
|               |                                                                                  |
| audioEncoding | AudioEncoding.PCM_8BIT                                                           |
|               | AudioEncoding.PCM_16BIT                                                          |
|               | AudioEncoding.PCM_32BIT, required 'sdk >= Build.VERSION_CODES.S'                 |
|               |                                                                                  |
| channels      | AudioFormat.CHANNEL_IN_MONO, other configurations from android.media.AudioFormat |

# Sample App
Checkout the sample [App](https://github.com/karya-inc/RawAudioRecorder/tree/main/app) for reference

<img src='https://github.com/karya-inc/RawAudioRecorder/assets/69595691/d4575a5d-cf95-4c2f-9cf9-48b285e2e271' alt='sample2' width='256'/>
<img src='https://github.com/karya-inc/RawAudioRecorder/assets/69595691/5cd9c003-9b88-4c4e-943e-2ffae9e797ae' alt='sample1' width='256'/>

