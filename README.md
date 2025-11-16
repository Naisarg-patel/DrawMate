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
    <th>
      Features:
    </th>
    <th>
      Screenshorts:
    </th>
  </tr>
  <tr>
    <td>
      <b>Canvas:</b>
      User can draw smooth stroke using fingure or stylus pen.
    </td>
    <td>canvas sc</td>
  </tr>
  <tr>
    <td><b>Brush Engine:</b>
      User can select multiple brushes with customizable properties like size, opacity of stroke. App has default manydDifferent strokes, textured strokes with customizable properties, User can change colour of stroke using colour picker tool
    also app provide live preview of stroke like which colour stoke has, size, opacity of stroke, textured of stroke and how stroke look like in canvas with selected brush image with selected brush name.
      <td> all brush options sc</td>
  </tr>
  <tr>
    <td><b>colour picker:</b>
    Using colour picker tools user can change colour of stroke and shapes.
    </td>
    <td>colour pickert sc</td>
  </tr>
  <tr>
    <td><b>Eraser:</b>
    Using eraser tool user can erase brush strokes, shape strokes and textured stroke. Application provide trasferent colour engine to erase pickel of strokes.
    </td>
    <td>eraser sc</td>
  </tr>
  <tr>
    <td><b>Shape tools:</b>
    Application provide user shapes like squrase, circles, straight line with adjestable boundries with colour option with stroke size.
    </td>
    <td>
      shape sc
    </td>
  </tr>
  <tr>
    <td><b>Undo or Redo:</b>
    User can undo or redo strokes, shapes, any erased stroke using undo or redo tools.provide Unlimited undo/redo history. 
    </td>
    <td> undo redo sc</td>
  </tr>
  <tr>
    <td><b>
      layer:</b>
      using layer user can access full canvas for drawing. layer tool hide tool bar for full canvas.
    </td>
    <td>
      layer sc
    </td>
  </tr>
  <tr>
    <td><b>
      new canvas:</b>
      user can create new blank canvas any time.
    </td>
    <td>
      new canvas sc
    </td>
  </tr>
  <tr>
    <td><b>
      save:</b>
          after completion of drawing user need to save drawing, so save tools provide save image as png formate in device with changeble name of image if user not want to change name system automatically apply date and time of saving as image name.
    </td>
    <td>
      save sc
    </td>
  </tr>
</table>
