# DrawMate – Android Drawing & Sketching App

DrawMate is a powerful, lightweight, and customizable drawing application built in Kotlin for Android.
It includes brush customization, textured brushes, shapes, eraser, and more.

## 🚀 Features:.
### 🎨 Drawing Tools:
- Smooth freehand drawing
- Custom brush engine
- Adjustable brush size & opacity
- Textured brushes (bitmap-based)
- Color picker (HSB wheel)
- Eraser tool

### 🖌️ Brush System:
- Basic brushes
- Halftone / textured / stamp brushes
- Live preview of brush stroke
- PNG texture support
- Configurable:
- Size
- Opacity
- Color
- Shape style
- Texture spacing

### 📐 Shapes Tool:
- Draw rectangle
- Draw circle
- Draw line
- Adjustable boundaries (drag to size)

### ↩️ Undo & Redo:
- Unlimited undo/redo history
- Works for brush strokes, shapes, fills

### 💾 File Management:
- Save artwork as PNG
- Set name of artwork
- Create a new sketch

## 🛠️ Tech Stack:

| Component    | Technology                                  |
| ------------ | ------------------------------------------- |
| Language     | Kotlin                                      |
| UI           | XML + Material Components                   |
| Drawing      | Canvas + BitmapShader + Custom brush engine |
| Color Picker | AmbilWarna                                  |
| Min SDK      | 26+                                         |
| Target SDK   | Latest                                      |

## Output:

<table>
  <tr>
    <th>Features</th>
    <th>Screenshots</th>
  </tr>

  <tr>
    <td>
      <b>Canvas:</b><br>
      Draw smooth and responsive strokes using finger or stylus. Optimized for low-latency, natural sketching.
    </td>
    <td><img width="350" height="650" alt="image" src="https://github.com/user-attachments/assets/e1e5096b-0a53-429f-810f-b908b706d113" />
</td>
  </tr>

  <tr>
    <td>
      <b>Brush Engine:</b><br>
      Choose from multiple brush types with fully customizable settings such as stroke size, opacity, texture, and color.
      Includes:<br>
      • Basic brushes<br>
      • Textured brushes<br>
      • Stamp / halftone brushes<br>
      • Live brush preview (color, size, opacity, texture, brush icon & name)
    </td>
    <td><img width="350" height="650" alt="image" src="https://github.com/user-attachments/assets/78f83d8a-443e-43af-8957-9c5b186f9aa5" />
    <img width="350" height="650" alt="image" src="https://github.com/user-attachments/assets/5800480b-0004-473b-949a-005850e549f9" />
<img width="350" height="650" alt="image" src="https://github.com/user-attachments/assets/3cae21d1-4385-4453-9aca-832546944f17" /></td>
  </tr>

  <tr>
    <td>
      <b>Color Picker:</b><br>
      Integrated HSB/HSV color wheel that allows users to pick any color instantly for both brushes and shapes.
    </td>
    <td>color_picker.png</td>
  </tr>

  <tr>
    <td>
      <b>Eraser:</b><br>
      High-precision eraser that supports pixel-level clearing using a transparent stroke engine. Works on all brush types and shapes.
    </td>
    <td>eraser.png</td>
  </tr>

  <tr>
    <td>
      <b>Shape Tools:</b><br>
      Draw adjustable geometric shapes including:<br>
      • Rectangle<br>
      • Circle / Ellipse<br>
      • Straight Line<br>
      Shapes can be resized dynamically and support color, stroke size, and opacity adjustments.
    </td>
    <td>shapes.png</td>
  </tr>

  <tr>
    <td>
      <b>Undo / Redo:</b><br>
      Unlimited undo/redo history for brush strokes, shapes, and eraser actions.
    </td>
    <td>undo_redo.png</td>
  </tr>

  <tr>
    <td>
      <b>Layer Mode:</b><br>
      Enables distraction-free drawing by hiding the toolbar and expanding the canvas to full screen. Tap again to restore tools.
    </td>
    <td>layer_mode.png</td>
  </tr>

  <tr>
    <td>
      <b>New Canvas:</b><br>
      Instantly create a fresh blank canvas without restarting the app.
    </td>
    <td>new_canvas.png</td>
  </tr>

  <tr>
    <td>
      <b>Save Artwork:</b><br>
      Save your drawing as a high-quality PNG image. Users can enter a custom file name, or the system will auto-generate one based on date and time.
    </td>
    <td>save.png</td>
  </tr>

</table>



