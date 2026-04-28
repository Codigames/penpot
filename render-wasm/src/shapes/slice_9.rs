use skia_safe::IRect;

/// 9-slice margins for image shapes, measured in source-image pixels.
#[derive(Debug, Clone, Copy)]
pub struct Slice9 {
    pub top: i32,
    pub right: i32,
    pub bottom: i32,
    pub left: i32,
}

impl Slice9 {
    pub fn new(top: i32, right: i32, bottom: i32, left: i32) -> Self {
        Self {
            top: top.max(0),
            right: right.max(0),
            bottom: bottom.max(0),
            left: left.max(0),
        }
    }

    pub fn has_margins(&self) -> bool {
        self.top > 0 || self.right > 0 || self.bottom > 0 || self.left > 0
    }

    /// Center rect in source image coordinates. Returns None when the
    /// margins do not leave a valid center region inside the source image.
    pub fn center_rect(&self, image_width: i32, image_height: i32) -> Option<IRect> {
        let l = self.left;
        let t = self.top;
        let r = image_width - self.right;
        let b = image_height - self.bottom;
        if r <= l || b <= t {
            None
        } else {
            Some(IRect::from_ltrb(l, t, r, b))
        }
    }
}
