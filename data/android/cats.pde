
float imageScale = 1;
private float time;
private int catCount = 12;
private ArrayList<Cat> cats = new ArrayList<Cat>();
private Cat held = null;
private PImage sticksIdle, sticksHeld, catHeld;
private PImage[] catDown, catRight, catUp;
int sticksFadeoutDelay = 60;
int sticksFadeoutDuration = 60;
int sticksLastReleasedFrame = - sticksFadeoutDuration*3;

private PGraphics pg;

public void settings() {
  fullScreen(P2D);
}

public void setup() {
  loadImages();
  pg = createGraphics(width, height, P2D);
  generateCats();
  pg.beginDraw();
  pg.colorMode(HSB, 1, 1, 1, 1);
  pg.background(0);
  pg.endDraw();
}

public void draw() {
  if (held != null && !mousePressed) {
    drop();
  }
  time = radians(frameCount);
  pg.beginDraw();
  pg.background(0);
  pg.imageMode(CENTER);
  updateWalkingCats();
  drawCursor();
  updateHeldCat();
  pg.endDraw();
  image(pg, 0, 0);
}

public void mouseReleased() {
  drop();
}

private void drop() {
  held = null;
  sticksLastReleasedFrame = frameCount;
}

private void loadImages() {
  sticksIdle = loadImage("hulky-idle.png");
  sticksHeld = loadImage("hulky-hold.png");
  catHeld = loadImage("kitt-held.png");
  catDown = new PImage[]{loadImage("kitt-down-1.png"), loadImage("kitt-down-2.png")};
  catRight = new PImage[]{loadImage("kitt-side-1.png"), loadImage("kitt-side-2.png")};
  catUp = new PImage[]{loadImage("kitt-up-1.png"), loadImage("kitt-up-2.png")};
}

private void updateHeldCat() {
  if (held == null) {
    return;
  }
  held.update();
}

private void drawCursor() {
  // noCursor();
  pg.pushStyle();
  float w = sticksHeld.width * imageScale;
  float h = sticksHeld.height * imageScale;
  pg.imageMode(CENTER);
  float x = mouseX + w * 0.37f;
  float y = mouseY + h * -0.37f;
  if (held == null) {
    float sticksFadeout = constrain(norm(frameCount-sticksFadeoutDelay, sticksLastReleasedFrame, 
      sticksLastReleasedFrame+sticksFadeoutDelay), 0, 1);
    pg.tint(1, 1-sticksFadeout);
    pg.image(sticksIdle, x, y, w, h);
  } else {
    pg.image(sticksHeld, x, y, w, h);
  }
  pg.popStyle();
}

void generateCats() {
  cats.clear();
  for (int i = 0; i < catCount; i++) {
    cats.add(new Cat());
  }
}

void updateWalkingCats() {
  for (Cat c : cats) {
    if (held == null || !held.equals(c)) {
      c.update();
    }
  }
}

class Cat {
  private PVector pos = new PVector(width / 2f + random(-10, 10), height / 2f + random(-10, 10));
  private int direction = floor(random(4));
  private float size = 62 * imageScale;
  private float hue = (.7f + random(.4f)) % 1;
  private float sat = random(.15f, .4f);
  private float br = random(.8f, 1);
  private float timeOffset = random(TAU);
  float speedMagnitude = random(.25f, .75f);

  void update() {
    mouseInteract();
    if (!thisHeld()) {
      updateDirection();
      move();
      checkCollisions();
    }
    draw();
  }

  private void move() {
    PVector speed = new PVector();
    if (direction == 0) {
      speed.x = 1;
    } else if (direction == 1) {
      speed.y = 1;
    } else if (direction == 2) {
      speed.x = -1;
    } else if (direction == 3) {
      speed.y = -1;
    }
    speed.mult(speedMagnitude);
    pos.add(speed);
    if (pos.x < size / 2f) {
      pos.x += width;
    }
    if (pos.x > width + size / 2f) {
      pos.x -= width;
    }
    if (pos.y < size / 2f) {
      pos.y += height;
    }
    if (pos.y > height + size / 2f) {
      pos.y -= height;
    }
  }

  private void updateDirection() {
    if (random(1) < 0.01f) {
      changeDirection();
    }
  }

  private void changeDirection() {
    if (random(1) > 0.5f) {
      direction++;
    } else {
      direction--;
    }
    while (direction < 0) {
      direction += 4;
    }
    direction %= 4;
  }

  private void draw() {
    pg.pushStyle();
    int frame = sin(time * 8 + timeOffset) > 0 ? 0 : 1;
    boolean flipHorizontally = direction == 2 && !thisHeld();
    PImage img = getImageByState(frame);
    pg.tint(hue, sat, br);
    drawCatAtPos(img, flipHorizontally);
    drawCatWrapAround(img, flipHorizontally);
    pg.popStyle();
  }

  private void drawCatAtPos(PImage img, boolean flipHorizontally) {
    pg.pushMatrix();
    pg.translate(pos.x, pos.y);
    flipIfNeeded(flipHorizontally);
    pg.image(img, 0, 0, size, size);
    pg.popMatrix();
  }

  private void drawCatWrapAround(PImage img, boolean flipHorizontally) {
    pg.pushMatrix();
    pg.translate(pos.x, pos.y);
    if (pos.x < size / 2f) {
      pg.translate(width, 0);
    }
    if (pos.x > width - size / 2f) {
      pg.translate(-width, 0);
    }
    if (pos.y < size / 2f) {
      pg.translate(0, height);
    }
    if (pos.y > height - size / 2f) {
      pg.translate(0, -height);
    }
    flipIfNeeded(flipHorizontally);
    pg.image(img, 0, 0, size, size);
    pg.popMatrix();
  }

  private PImage getImageByState(int frame) {
    if (thisHeld()) {
      return catHeld;
    }
    if (direction == 0 || direction == 2) {
      return catRight[frame];
    } else if (direction == 1) {
      return catDown[frame];
    } else if (direction == 3) {
      return catUp[frame];
    }
    return catHeld;
  }

  private void flipIfNeeded(boolean flipX) {
    if (flipX) {
      pg.scale(-1, 1);
    } else {
      pg.scale(1, 1);
    }
  }

  private boolean thisHeld() {
    if (held == null) {
      return false;
    }
    return held.equals(this);
  }

  void mouseInteract() {
    if (held == null && mousePressed && dist(mouseX, mouseY, pos.x, pos.y) < size / 2) {
      held = this;
    }
    if (held != null && held.equals(this)) {
      pos.x = lerp(pos.x, mouseX, .5f);
      pos.y = lerp(pos.y, mouseY, .5f);
    }
  }

  void checkCollisions() {
    for (Cat otherCard : cats) {
      if (otherCard.equals(this)) {
        continue;
      }
      float distanceToOther = dist(pos.x, pos.y, otherCard.pos.x, otherCard.pos.y);
      if (distanceToOther < size) {
        PVector fromOtherToThis = PVector.sub(pos, otherCard.pos);
        float repulsion = (1 / norm(distanceToOther, 0, size)) * .5f;
        pos.add(fromOtherToThis.normalize().mult(repulsion));
      }
    }
  }
}
